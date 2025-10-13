package com.rodionov.center.presentation.participant_list

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.participant_list.ParticipantListState
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class ParticipantListViewModel(
    private val repository: OrienteeringCompetitionLocalRepository
): BaseViewModel<ParticipantListState>(ParticipantListState()) {

    override fun onAction(action: BaseAction) {

    }

    fun getCompetitionDetails(competitionId: Long) {
        viewModelScope.launch {
            repository.getCompetitionWithDetails(competitionId).onSuccess {
                updateState { copy(participantGroupWithParticipants = it.groupsWithParticipants) }
            }
        }
    }
}