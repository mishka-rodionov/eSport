package com.competra.center.presentation.read_card

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.read_card.CheckResult
import com.competra.center.data.read_card.OrientReadCardState
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.getArguments
import com.competra.domain.models.ParticipantGroup
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.CompetitionStatus
import com.competra.domain.models.orienteering.ControlPoint
import com.competra.domain.models.orienteering.ControlPointRole
import com.competra.domain.models.orienteering.OrienteeringDirection
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.ReadChipData
import com.competra.domain.models.orienteering.ResultConflictEvent
import com.competra.domain.models.orienteering.SplitTime
import com.competra.domain.repository.ResultConflictRepository
import com.competra.nfchelper.SportiduinoHelper
import com.competra.center.data.read_card.OrientReadCardAction
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val resultConflictRepository: ResultConflictRepository,
    navigation: Navigation,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<OrientReadCardState>(OrientReadCardState()) {

    val competitionId: String? = navigation.getArguments<String>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {
        if (action !is OrientReadCardAction) return
        when (action) {
            is OrientReadCardAction.EditSplitClicked -> {
                updateState { copy(editingSplitIndex = action.index) }
            }
            is OrientReadCardAction.DismissEditSplit -> {
                updateState { copy(editingSplitIndex = null) }
            }
            is OrientReadCardAction.SaveSplitEdit -> {
                analytics.trackEvent(AnalyticsEvent.SplitEditSaved)
                val updated = stateValue.rawSplits?.toMutableList() ?: return
                updated[action.index] = updated[action.index].copy(timestamp = action.newTimestamp)
                updateState { copy(rawSplits = updated) }
                viewModelScope.launch(Dispatchers.IO) { recalculateAndSaveResult() }
            }
            is OrientReadCardAction.DeleteSplit -> {
                analytics.trackEvent(AnalyticsEvent.SplitDeleted)
                val updated = stateValue.rawSplits?.toMutableList() ?: return
                updated.removeAt(action.index)
                updateState { copy(rawSplits = updated) }
                viewModelScope.launch(Dispatchers.IO) { recalculateAndSaveResult() }
            }
            is OrientReadCardAction.CreditMissedCp -> {
                val updated = stateValue.rawSplits?.toMutableList() ?: return
                updated.add(SplitTime(controlPoint = action.cpNumber, timestamp = action.prevTimestamp))
                updated.sortBy { it.timestamp }
                updateState { copy(rawSplits = updated) }
                viewModelScope.launch(Dispatchers.IO) { recalculatePending() }
            }
            is OrientReadCardAction.SaveResult -> {
                viewModelScope.launch(Dispatchers.IO) { saveResultFromPending() }
            }
        }
    }

    private suspend fun recalculateAndSaveResult() {
        val participant = stateValue.participant ?: return
        val rawSplits = stateValue.rawSplits ?: return
        if (rawSplits.isEmpty()) return
        val expected = getExpectedControlPoints(participant.groupId)
        val checkResult = computeCheckResult(participant.groupId, participant.startTime, expected, rawSplits)
        val lastValidPunch = checkResult.validSplits.lastOrNull() ?: rawSplits.last()
        val finishTime = lastValidPunch.timestamp
        val totalTime = (finishTime - participant.startTime) / 1000L
        val newResult = OrienteeringResult(
            competitionId = participant.competitionId,
            participantId = participant.id,
            groupId = participant.groupId,
            startTime = participant.startTime,
            finishTime = finishTime,
            totalTime = totalTime,
            rank = -1,
            status = checkResult.status,
            penaltyTime = 0,
            totalScore = checkResult.totalScore,
            scorePenalty = checkResult.scorePenalty,
            splits = checkResult.validSplits,
            isEdited = true
        )
        updateState { copy(participantResult = newResult, editingSplitIndex = null) }
        val existing = orienteeringCompetitionInteractor.getResultByParticipantId(participant.id)
        if (existing != null) {
            orienteeringCompetitionInteractor.applyConflictResult(existing.id, newResult)
        } else {
            orienteeringCompetitionInteractor.saveParticipantResult(newResult)
        }
        refreshGroupRank(participant)
    }

    private suspend fun refreshGroupRank(participant: OrienteeringParticipant) {
        val cid = competitionId ?: return
        val updatedResult = orienteeringCompetitionInteractor.getResultByParticipantId(participant.id)
        val groupRank = updatedResult?.rank?.takeIf { it > 0 }
        val groupsData = orienteeringCompetitionInteractor.getResultsByGroups(cid).getOrNull()
        val groupParticipants = groupsData?.firstOrNull { it.group.groupId == participant.groupId }?.participants
        val totalFinished = groupParticipants?.count { it.result?.status == ResultStatus.FINISHED } ?: 0
        updateState { copy(groupRank = groupRank, groupTotalFinished = totalFinished) }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            competitionId?.let { id ->
                val competition = orienteeringCompetitionInteractor.getCompetition(id)
                if (competition?.competition?.status == CompetitionStatus.FINISHED) {
                    updateState { copy(isCompetitionFinished = true) }
                }
                competition?.direction?.let { direction ->
                    updateState { copy(competitionDirection = direction) }
                }
            }
            sportiduinoHelper.subscribeToReadCard { chipData ->
                handleChipData(chipData)
            }
        }
    }

    fun handleChipData(chipData: ReadChipData) {
        // После завершения соревнования чип всё ещё можно отсканировать, но данные
        // показываются только для просмотра и не сохраняются (см. createParticipantResult).
        val cardType = when (chipData) {
            is ReadChipData.RawResult -> AnalyticsEvent.NfcCardType.PARTICIPANT
            is ReadChipData.MasterChipData -> AnalyticsEvent.NfcCardType.MASTER
        }
        analytics.trackEvent(AnalyticsEvent.NfcChipReadAttempted(cardType))
        when (chipData) {
            is ReadChipData.RawResult -> {
                competitionId?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        orienteeringCompetitionInteractor.getParticipantByChipNumber(
                            competitionId = competitionId,
                            chipNumber = chipData.chipNumber
                        ).onSuccess { participant ->
                            analytics.trackEvent(AnalyticsEvent.NfcChipReadSuccess)
                            computeParticipantResult(
                                participant = participant,
                                rawResult = chipData
                            )
                        }.onFailure {
                            analytics.trackEvent(AnalyticsEvent.NfcChipReadFailed("participant_not_found"))
                        }
                    }
                }
            }

            is ReadChipData.MasterChipData -> {
                analytics.trackEvent(AnalyticsEvent.NfcChipReadSuccess)
            }
        }
    }

    suspend fun getExpectedControlPoints(groupId: Long): List<ControlPoint> {
        val group = orienteeringCompetitionInteractor.getParticipantGroup(groupId).getOrNull()
            ?: return emptyList()
        val distance = orienteeringCompetitionInteractor.getDistanceById(group.distanceId).getOrNull()
            ?: return emptyList()
        val base = distance.controlPoints
        val finishNumber = distance.finishControlPoint
        return if (finishNumber != null) {
            base + ControlPoint(number = finishNumber, role = ControlPointRole.FINISH)
        } else {
            base
        }
    }

    suspend fun computeParticipantResult(
        participant: OrienteeringParticipant,
        rawResult: ReadChipData.RawResult
    ) {
        val splits = rawResult.splits
        Log.d("LOG_TAG", "computeParticipantResult: $splits")
        if (splits.isEmpty()) return
        val expected = getExpectedControlPoints(participant.groupId)
        Log.d("LOG_TAG", "computeParticipantResult: $expected")
        val expectedCpNumbers = expected.map { it.number }
        val result = computeCheckResult(
            groupId = participant.groupId,
            startTime = participant.startTime,
            expected = expected,
            actual = splits
        )
        val lastValidPunch = result.validSplits.lastOrNull() ?: splits.last()
        val finishTime = lastValidPunch.timestamp
        val totalTime = (finishTime - participant.startTime) / 1000L
        createParticipantResult(
            participant = participant,
            finishTime = finishTime,
            totalTime = totalTime,
            result = result,
            rawSplits = splits,
            expectedCpNumbers = expectedCpNumbers
        )
    }

    private suspend fun createParticipantResult(
        participant: OrienteeringParticipant,
        finishTime: Long,
        totalTime: Long,
        result: CheckResult,
        rawSplits: List<SplitTime>,
        expectedCpNumbers: List<Int> = emptyList()
    ) {
        val newResult = OrienteeringResult(
            competitionId = participant.competitionId,
            participantId = participant.id,
            groupId = participant.groupId,
            startTime = participant.startTime,
            finishTime = finishTime,
            totalTime = totalTime,
            rank = -1,
            status = result.status,
            penaltyTime = 0,
            totalScore = result.totalScore,
            scorePenalty = result.scorePenalty,
            splits = if (result.status == ResultStatus.DSQ) rawSplits else result.validSplits
        )

        if (stateValue.isCompetitionFinished) {
            // Соревнование завершено — режим «только просмотр»: показываем данные,
            // но не сохраняем результат и не влияем на итоговые протоколы.
            updateState {
                copy(
                    participant = participant,
                    participantResult = newResult,
                    rawSplits = rawSplits,
                    expectedCpNumbers = expectedCpNumbers,
                    isPendingSave = false,
                )
            }
            return
        }

        if (newResult.status == ResultStatus.DSQ) {
            // DSQ: показываем результат организатору и ждём явного сохранения
            updateState {
                copy(
                    participant = participant,
                    participantResult = newResult,
                    rawSplits = rawSplits,
                    expectedCpNumbers = expectedCpNumbers,
                    isPendingSave = true,
                )
            }
            return
        }

        updateState {
            copy(
                participant = participant,
                participantResult = newResult,
                rawSplits = rawSplits,
                expectedCpNumbers = expectedCpNumbers
            )
        }

        val existing = orienteeringCompetitionInteractor.getResultByParticipantId(participant.id)

        if (existing != null) {
            // Уже есть запись — эмитим конфликт для отображения диалога в MainActivity
            resultConflictRepository.emit(
                ResultConflictEvent(
                    participantName = "${participant.lastName} ${participant.firstName}",
                    existingResult = existing,
                    newResult = newResult,
                    onApply = {
                        orienteeringCompetitionInteractor.applyConflictResult(existing.id, newResult)
                    }
                )
            )
        } else {
            orienteeringCompetitionInteractor.saveParticipantResult(newResult)
            refreshGroupRank(participant)
        }
    }

    /** Пересчитывает результат после «Засчитать» без записи в БД. */
    private suspend fun recalculatePending() {
        val participant = stateValue.participant ?: return
        val rawSplits = stateValue.rawSplits ?: return
        if (rawSplits.isEmpty()) return
        val expected = getExpectedControlPoints(participant.groupId)
        val checkResult = computeCheckResult(participant.groupId, participant.startTime, expected, rawSplits)
        val lastValidPunch = checkResult.validSplits.lastOrNull() ?: rawSplits.last()
        val finishTime = lastValidPunch.timestamp
        val totalTime = (finishTime - participant.startTime) / 1000L
        val newResult = OrienteeringResult(
            competitionId = participant.competitionId,
            participantId = participant.id,
            groupId = participant.groupId,
            startTime = participant.startTime,
            finishTime = finishTime,
            totalTime = totalTime,
            rank = -1,
            status = checkResult.status,
            penaltyTime = 0,
            totalScore = checkResult.totalScore,
            scorePenalty = checkResult.scorePenalty,
            splits = if (checkResult.status == ResultStatus.DSQ) rawSplits else checkResult.validSplits,
            isEdited = true,
        )
        updateState { copy(participantResult = newResult) }
    }

    /** Явное сохранение результата организатором (вызывается по кнопке «Сохранить результат»). */
    private suspend fun saveResultFromPending() {
        val participant = stateValue.participant ?: return
        val newResult = stateValue.participantResult ?: return
        val existing = orienteeringCompetitionInteractor.getResultByParticipantId(participant.id)
        if (existing != null) {
            resultConflictRepository.emit(
                ResultConflictEvent(
                    participantName = "${participant.lastName} ${participant.firstName}",
                    existingResult = existing,
                    newResult = newResult,
                    onApply = {
                        orienteeringCompetitionInteractor.applyConflictResult(existing.id, newResult)
                    }
                )
            )
        } else {
            orienteeringCompetitionInteractor.saveParticipantResult(newResult)
            refreshGroupRank(participant)
        }
        updateState { copy(isPendingSave = false) }
    }

    /** Выбирает алгоритм проверки отметок в зависимости от формата соревнования. */
    private suspend fun computeCheckResult(
        groupId: Long,
        startTime: Long,
        expected: List<ControlPoint>,
        actual: List<SplitTime>
    ): CheckResult {
        if (stateValue.competitionDirection != OrienteeringDirection.BY_CHOICE) {
            return checkControlPointOrderPro(expected, actual)
        }
        val group = orienteeringCompetitionInteractor.getParticipantGroup(groupId).getOrNull()
            ?: return CheckResult(ResultStatus.DSQ, "Группа участника не найдена")
        return computeByChoiceResult(expected, actual, startTime, group)
    }

    fun checkControlPointOrderPro(
        expected: List<ControlPoint>,
        actual: List<SplitTime>
    ): CheckResult {

        if (expected.isEmpty()) {
            return CheckResult(ResultStatus.DSQ, "Для группы не заданы КП")
        }

        if (actual.isEmpty()) {
            return CheckResult(ResultStatus.DSQ, "В чипе нет отметок")
        }

        val expectedNumbers = expected.map { it.number }

        var searchIndex = 0
        val validSplits = mutableListOf<SplitTime>()

        for (expectedCp in expectedNumbers) {

            var found = false

            for (i in searchIndex until actual.size) {
                val punch = actual[i]

                if (punch.controlPoint == expectedCp) {
                    validSplits.add(punch)
                    searchIndex = i + 1
                    found = true
                    break
                }
            }

            if (!found) {
                return CheckResult(
                    ResultStatus.DSQ,
                    "Пропущен КП $expectedCp"
                )
            }
        }

        return CheckResult(
            status = ResultStatus.FINISHED,
            validSplits = validSplits
        )
    }

}

/**
 * Проверка отметок для формата «по выбору» (score-О): порядок взятия КП не важен,
 * повторные отметки одного КП засчитываются один раз, обязательные (REQUIRED) КП должны
 * быть взяты все — иначе DSQ. Результат — сумма баллов за минусом штрафа за опоздание
 * сверх лимита времени группы; при превышении порога сильного опоздания баллы обнуляются.
 *
 * Вынесена в top-level функцию (а не метод [OrientReadCardViewModel]), т.к. не использует
 * состояние ViewModel — это упрощает unit-тестирование без мокирования зависимостей.
 */
fun computeByChoiceResult(
    expected: List<ControlPoint>,
    actual: List<SplitTime>,
    startTime: Long,
    group: ParticipantGroup
): CheckResult {
    if (expected.isEmpty()) {
        return CheckResult(ResultStatus.DSQ, "Для группы не заданы КП")
    }
    if (actual.isEmpty()) {
        return CheckResult(ResultStatus.DSQ, "В чипе нет отметок")
    }

    val expectedNumbers = expected.map { it.number }.toSet()
    val dedupedSplits = actual
        .sortedBy { it.timestamp }
        .distinctBy { it.controlPoint }
        .filter { it.controlPoint in expectedNumbers }

    val requiredNumbers = expected.filter { it.role == ControlPointRole.REQUIRED }.map { it.number }
    val takenNumbers = dedupedSplits.map { it.controlPoint }.toSet()
    val missingRequired = requiredNumbers.firstOrNull { it !in takenNumbers }
    if (missingRequired != null) {
        return CheckResult(ResultStatus.DSQ, "Пропущен обязательный КП $missingRequired")
    }

    val scoreByNumber = expected.associate { it.number to it.score }
    val rawScore = dedupedSplits.sumOf { scoreByNumber[it.controlPoint] ?: 0 }

    val lastPunch = dedupedSplits.lastOrNull() ?: actual.last()
    val totalTimeSeconds = (lastPunch.timestamp - startTime) / 1000L

    val timeLimitMinutes = group.timeLimitMinutes
    var scorePenalty = 0
    var finalScore = rawScore

    if (timeLimitMinutes != null) {
        val lateSeconds = totalTimeSeconds - timeLimitMinutes * 60L
        val lateMinutes = if (lateSeconds > 0) ceil(lateSeconds / 60.0).toInt() else 0

        if (lateMinutes > 0) {
            scorePenalty = lateMinutes * (group.scorePenaltyPerMinute ?: 0)
            finalScore = (rawScore - scorePenalty).coerceAtLeast(0)
        }

        val maxLateness = group.maxLatenessMinutes
        if (maxLateness != null && lateMinutes > maxLateness) {
            finalScore = 0
        }
    }

    return CheckResult(
        status = ResultStatus.FINISHED,
        validSplits = dedupedSplits,
        totalScore = finalScore,
        scorePenalty = scorePenalty
    )
}