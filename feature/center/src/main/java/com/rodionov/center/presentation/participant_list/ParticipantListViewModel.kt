package com.rodionov.center.presentation.participant_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.participant_list.ParticipantListState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.launch

class ParticipantListViewModel(
    private val repository: OrienteeringCompetitionLocalRepository,
    private val navigation: Navigation
): BaseViewModel<ParticipantListState>(ParticipantListState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {

    }

    fun getCompetitionDetails() {
        Log.d("LOG_TAG", "getCompetitionDetails: competitionId = $competitionId")
        viewModelScope.launch {
            competitionId?.let { compId ->
                repository.getCompetitionWithDetails(compId).onSuccess {
                    updateState { copy(participantGroupWithParticipants = it.groupsWithParticipants) }
                }
            }
        }
    }
}