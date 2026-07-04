package com.competra.eventdetails.presentation.race_graph

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.domain.models.ParticipantGroup
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.domain.models.orienteering.buildRaceGraphData
import com.competra.domain.models.orienteering.buildSplitsTable
import com.competra.domain.models.orienteering.sortedForResults
import com.competra.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EventRaceGraphViewModel(
    private val remoteRepository: OrienteeringCompetitionRemoteRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventRaceGraphState>(EventRaceGraphState()) {

    override fun onAction(action: BaseAction) {}

    /**
     * [groupId] здесь — это [ParticipantGroup.remoteId], а не [ParticipantGroup.groupId] —
     * см. тот же паттерн в [com.competra.eventdetails.presentation.group_splits.EventGroupSplitsTableViewModel].
     */
    fun load(eventId: String, groupId: Long) {
        analytics.trackEvent(AnalyticsEvent.RaceGraphOpened(groupId, eventId))
        viewModelScope.launch(Dispatchers.IO) {
            val groupsDeferred = async { remoteRepository.getCompetitionParticipantsGroups(eventId).getOrNull() ?: emptyList() }
            val participantsDeferred = async { remoteRepository.getParticipantsForCompetition(eventId).getOrNull() ?: emptyList() }
            val resultsDeferred = async { remoteRepository.getResultsByCompetition(eventId).getOrNull() ?: emptyList() }

            val group = groupsDeferred.await().firstOrNull { it.remoteId == groupId }
            if (group == null) {
                updateState { copy(isLoading = false) }
                return@launch
            }

            val participants = participantsDeferred.await()
            val results = resultsDeferred.await()

            val resultsByParticipant = results.associateBy { it.participantId }
            val groupParticipants = participants.filter { it.groupId == group.remoteId }
            val participantsWithResults = groupParticipants.map { participant ->
                ParticipantWithResult(
                    participant = participant,
                    result = resultsByParticipant[participant.id]
                )
            }.sortedForResults()

            val table = buildSplitsTable(
                GroupWithParticipantsAndResults(group = group, participants = participantsWithResults)
            )
            val raceGraphData = buildRaceGraphData(table)

            val defaultVisible = raceGraphData.series
                .sortedBy { it.result?.rank ?: Int.MAX_VALUE }
                .take(EVENT_RACE_GRAPH_DEFAULT_VISIBLE_COUNT)
                .map { it.participant.id }
                .toSet()

            updateState {
                copy(
                    groupTitle = group.title,
                    data = raceGraphData,
                    visibleParticipantIds = defaultVisible,
                    isLoading = false,
                )
            }
        }
    }

    fun toggleParticipantVisibility(participantId: String) {
        updateState {
            val visible = if (participantId in visibleParticipantIds) {
                visibleParticipantIds - participantId
            } else {
                visibleParticipantIds + participantId
            }
            copy(visibleParticipantIds = visible)
        }
    }

    fun toggleHighlight(participantId: String) {
        updateState {
            copy(highlightedParticipantId = if (highlightedParticipantId == participantId) null else participantId)
        }
    }
}
