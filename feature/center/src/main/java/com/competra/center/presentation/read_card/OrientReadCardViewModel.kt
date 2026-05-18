package com.competra.center.presentation.read_card

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.read_card.CheckResult
import com.competra.center.data.read_card.OrientReadCardState
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.getArguments
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.CompetitionStatus
import com.competra.domain.models.orienteering.ControlPoint
import com.competra.domain.models.orienteering.ControlPointRole
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.ReadChipData
import com.competra.domain.models.orienteering.ResultConflictEvent
import com.competra.domain.models.orienteering.SplitTime
import com.competra.domain.repository.ResultConflictRepository
import com.competra.nfchelper.SportiduinoHelper
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val resultConflictRepository: ResultConflictRepository,
    navigation: Navigation
) : BaseViewModel<OrientReadCardState>(OrientReadCardState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            competitionId?.let { id ->
                val competition = orienteeringCompetitionInteractor.getCompetition(id)
                if (competition?.competition?.status == CompetitionStatus.FINISHED) {
                    updateState { copy(isCompetitionFinished = true) }
                }
            }
            sportiduinoHelper.subscribeToReadCard { chipData ->
                handleChipData(chipData)
            }
        }
    }

    fun handleChipData(chipData: ReadChipData) {
        if (stateValue.isCompetitionFinished) return
        when (chipData) {
            is ReadChipData.RawResult -> {
                competitionId?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        orienteeringCompetitionInteractor.getParticipantByChipNumber(
                            competitionId = competitionId,
                            chipNumber = chipData.chipNumber
                        ).onSuccess { participant ->
                            computeParticipantResult(
                                participant = participant,
                                rawResult = chipData
                            )
                        }
                    }
                }
            }

            is ReadChipData.MasterChipData -> {}
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
        val result = checkControlPointOrderPro(
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
            rawSplits = splits
        )
    }

    private suspend fun createParticipantResult(
        participant: OrienteeringParticipant,
        finishTime: Long,
        totalTime: Long,
        result: CheckResult,
        rawSplits: List<SplitTime>
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
            splits = result.validSplits
        )

        updateState { copy(participant = participant, participantResult = newResult, rawSplits = rawSplits) }

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
        }
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