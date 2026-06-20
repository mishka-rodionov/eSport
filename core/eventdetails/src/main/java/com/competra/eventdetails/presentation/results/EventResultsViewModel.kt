package com.competra.eventdetails.presentation.results

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.competra.ui.BaseAction
import com.competra.ui.BaseState
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class EventResultsState(
    val isLoading: Boolean = true,
    val groupsWithResults: List<GroupWithParticipantsAndResults> = emptyList(),
    val selectedParticipant: ParticipantWithResult? = null
) : BaseState

sealed interface EventResultsAction : BaseAction {
    data class ShowSplits(val participant: ParticipantWithResult) : EventResultsAction
    data object HideSplits : EventResultsAction
}

class EventResultsViewModel(
    private val remoteRepository: OrienteeringCompetitionRemoteRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventResultsState>(EventResultsState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventResultsAction.ShowSplits -> updateState { copy(selectedParticipant = action.participant) }
            is EventResultsAction.HideSplits -> updateState { copy(selectedParticipant = null) }
        }
    }

    fun loadResults(eventId: String) {
        analytics.trackEvent(AnalyticsEvent.ResultsViewed(eventId))
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isLoading = true) }

            val groupsDeferred = async { remoteRepository.getCompetitionParticipantsGroups(eventId).getOrNull() ?: emptyList() }
            val participantsDeferred = async { remoteRepository.getParticipantsForCompetition(eventId).getOrNull() ?: emptyList() }
            val resultsDeferred = async { remoteRepository.getResultsByCompetition(eventId).getOrNull() ?: emptyList() }

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

            updateState { copy(isLoading = false, groupsWithResults = groupsWithResults) }
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
