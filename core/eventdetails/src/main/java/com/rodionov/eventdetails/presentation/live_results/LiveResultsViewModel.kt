package com.rodionov.eventdetails.presentation.live_results

import androidx.lifecycle.viewModelScope
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val REFRESH_INTERVAL_MS = 30_000L

data class LiveResultsState(
    val groupsWithResults: List<GroupWithParticipantsAndResults> = emptyList(),
    val isLoading: Boolean = true,
    val lastUpdated: Long? = null,
    val selectedParticipant: ParticipantWithResult? = null
) : BaseState

sealed interface LiveResultsAction : BaseAction {
    data class ShowSplits(val participant: ParticipantWithResult) : LiveResultsAction
    data object HideSplits : LiveResultsAction
}

/**
 * ViewModel экрана онлайн-результатов соревнования.
 * Автоматически обновляет данные каждые [REFRESH_INTERVAL_MS] мс.
 */
class LiveResultsViewModel(
    private val remoteRepository: OrienteeringCompetitionRemoteRepository
) : BaseViewModel<LiveResultsState>(LiveResultsState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is LiveResultsAction.ShowSplits -> updateState { copy(selectedParticipant = action.participant) }
            is LiveResultsAction.HideSplits -> updateState { copy(selectedParticipant = null) }
        }
    }

    fun startPolling(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                loadResults(eventId)
                delay(REFRESH_INTERVAL_MS)
            }
        }
    }

    private suspend fun loadResults(eventId: Long) {
        val groupsDeferred = viewModelScope.async(Dispatchers.IO) {
            remoteRepository.getCompetitionParticipantsGroups(eventId).getOrNull() ?: emptyList()
        }
        val participantsDeferred = viewModelScope.async(Dispatchers.IO) {
            remoteRepository.getParticipantsForCompetition(eventId).getOrNull() ?: emptyList()
        }
        val resultsDeferred = viewModelScope.async(Dispatchers.IO) {
            remoteRepository.getResultsByCompetition(eventId).getOrNull() ?: emptyList()
        }

        val groups = groupsDeferred.await()
        val participants = participantsDeferred.await()
        val results = resultsDeferred.await()

        val resultsByParticipant = results.associateBy { it.participantId }
        val participantsByGroup = participants.groupBy { it.groupId }

        val groupsWithResults = groups.map { group ->
            val groupParticipants = participantsByGroup[group.remoteId] ?: emptyList()
            val participantsWithResults = groupParticipants.map { participant ->
                ParticipantWithResult(
                    participant = participant,
                    result = resultsByParticipant[participant.id]
                )
            }.sortedWith(
                compareBy(
                    { statusSortOrder(it.result?.status) },
                    { it.result?.totalTime ?: Long.MAX_VALUE }
                )
            )
            GroupWithParticipantsAndResults(group = group, participants = participantsWithResults)
        }

        updateState {
            copy(
                groupsWithResults = groupsWithResults,
                isLoading = false,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    private fun statusSortOrder(status: ResultStatus?): Int = when (status) {
        ResultStatus.FINISHED -> 0
        ResultStatus.DSQ -> 1
        ResultStatus.DNF -> 2
        ResultStatus.DNS -> 3
        ResultStatus.STARTED -> 4
        ResultStatus.REGISTERED -> 5
        null -> 9
    }
}
