package com.competra.eventdetails.presentation.group_splits

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.domain.models.ParticipantGroup
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.domain.models.orienteering.buildSplitsTable
import com.competra.domain.models.orienteering.sortedForResults
import com.competra.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EventGroupSplitsTableViewModel(
    private val remoteRepository: OrienteeringCompetitionRemoteRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventGroupSplitsTableState>(EventGroupSplitsTableState()) {

    override fun onAction(action: BaseAction) {}

    /**
     * [groupId] здесь — это [ParticipantGroup.remoteId], а не [ParticipantGroup.groupId]:
     * в этом remote-only потоке (публичный просмотр события) локальный groupId не заполняется
     * маппером ответа сервера (`ParticipantGroupResponse.toDomain()` всегда ставит 0) — реальный
     * уникальный идентификатор группы приходит только в remoteId. См. тот же паттерн
     * в [com.competra.eventdetails.presentation.results.EventResultsViewModel.loadResults].
     */
    fun load(eventId: String, groupId: Long) {
        analytics.trackEvent(AnalyticsEvent.GroupSplitsTableOpened(groupId, eventId))
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
            updateState { copy(groupTitle = group.title, table = table, isLoading = false) }
        }
    }
}
