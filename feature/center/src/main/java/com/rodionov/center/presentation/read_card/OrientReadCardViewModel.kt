package com.rodionov.center.presentation.read_card

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.read_card.CheckResult
import com.rodionov.center.data.read_card.OrientReadCardState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.ReadChipData
import com.rodionov.domain.models.orienteering.SplitTime
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    navigation: Navigation
) : BaseViewModel<OrientReadCardState>(OrientReadCardState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sportiduinoHelper.subscribeToReadCard { chipData ->
                handleChipData(chipData)
//                updateState { copy(text = text) }
            }
        }
    }

    fun handleChipData(chipData: ReadChipData) {
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
        return group?.controlPoints ?: emptyList()
    }

    suspend fun computeParticipantResult(
        participant: OrienteeringParticipant,
        rawResult: ReadChipData.RawResult
    ) {
        val splits = rawResult.splits
        val cpOrder = rawResult.splits.map { it.controlPoint }
        val lastPunch = splits.lastOrNull() ?: return
        val totalTime = lastPunch.timestamp - participant.startTime
        val expected = getExpectedControlPoints(participant.groupId)
        val result = checkControlPointOrderPro(
            expected = expected,
            actual = splits
        )
        createParticipantResult(
            participant = participant,
            finishTime = lastPunch.timestamp,
            totalTime = totalTime,
            result = result,
        )
    }

    private suspend fun createParticipantResult(
        participant: OrienteeringParticipant,
        finishTime: Long,
        totalTime: Long,
        result: CheckResult
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
        updateState {
            copy(
                participant = participant,
                participantResult = newResult
            )
        }
        orienteeringCompetitionInteractor.saveParticipantResult(newResult)
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