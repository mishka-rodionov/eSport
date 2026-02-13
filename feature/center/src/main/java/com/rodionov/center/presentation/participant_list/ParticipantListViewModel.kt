package com.rodionov.center.presentation.participant_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.participant_list.ParticipantListAction
import com.rodionov.center.data.participant_list.ParticipantListState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParticipantListViewModel(
    private val repository: OrienteeringCompetitionLocalRepository,
    private val competitionInteractor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
): BaseViewModel<ParticipantListState>(ParticipantListState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)
    var startNumber = 1

    override fun onAction(action: BaseAction) {
        when(action) {
            is ParticipantListAction.ShowCreateParticipantDialog -> {
                updateState { copy(group = action.group, isShowParticipantCreateDialog = true) }
            }
            is ParticipantListAction.CreateNewParticipant -> {
                val group = stateValue.participantGroupWithParticipants[action.group].group
                val participant = OrienteeringParticipant(
                    id = (0..1000L).random(),
                    userId = "",
                    firstName = action.firstName,
                    lastName = action.secondName,
                    groupId = group.groupId,
                    competitionId = group.competitionId,
                    commandName = "",
                    startNumber = startNumber++.toString(),
                    startTime = 10L,
                    chipNumber = "",
                    comment = ""
                )
                viewModelScope.launch(Dispatchers.IO) {
                    val participant = competitionInteractor.saveParticipant(participant)
                    if (participant != null) {
                        getCompetitionDetails()
                    }
                }
            }
            ParticipantListAction.HideCreateParticipantDialog -> {
                updateState { copy(isShowParticipantCreateDialog = false) }
            }
        }
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