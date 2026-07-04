package com.competra.eventdetails.presentation.results

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.EventsNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.domain.models.orienteering.sortedForResults
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
    data class OpenGroupSplitsTable(val eventId: String, val groupId: Long) : EventResultsAction
    data class OpenRaceGraph(val eventId: String, val groupId: Long) : EventResultsAction
}

class EventResultsViewModel(
    private val remoteRepository: OrienteeringCompetitionRemoteRepository,
    private val analytics: AnalyticsTracker,
    private val navigation: Navigation,
) : BaseViewModel<EventResultsState>(EventResultsState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is EventResultsAction.ShowSplits -> updateState { copy(selectedParticipant = action.participant) }
            is EventResultsAction.HideSplits -> updateState { copy(selectedParticipant = null) }
            is EventResultsAction.OpenGroupSplitsTable -> openGroupSplitsTable(action.eventId, action.groupId)
            is EventResultsAction.OpenRaceGraph -> openRaceGraph(action.eventId, action.groupId)
        }
    }

    private fun openGroupSplitsTable(eventId: String, groupId: Long) {
        viewModelScope.launch {
            navigation.navigate(EventsNavigation.EventSplitsTableRoute(eventId, groupId))
        }
    }

    private fun openRaceGraph(eventId: String, groupId: Long) {
        viewModelScope.launch {
            navigation.navigate(EventsNavigation.EventRaceGraphRoute(eventId, groupId))
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
                }.sortedForResults()
                GroupWithParticipantsAndResults(group = group, participants = participantsWithResults)
            }

            updateState { copy(isLoading = false, groupsWithResults = groupsWithResults) }
        }
    }
}
