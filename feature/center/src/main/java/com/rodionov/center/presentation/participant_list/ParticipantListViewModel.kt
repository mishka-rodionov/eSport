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

/**
 * ViewModel для экрана списка участников.
 */
class ParticipantListViewModel(
    private val repository: OrienteeringCompetitionLocalRepository,
    private val competitionInteractor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
): BaseViewModel<ParticipantListState>(ParticipantListState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {
        when(action) {
            is ParticipantListAction.ShowCreateParticipantDialog -> {
                updateState { copy(group = action.group, editingParticipant = null, isShowParticipantCreateDialog = true) }
            }
            is ParticipantListAction.ShowEditParticipantDialog -> {
                updateState { copy(group = action.group, editingParticipant = action.participant, isShowParticipantCreateDialog = true) }
            }
            is ParticipantListAction.CreateNewParticipant -> {
                val groupData = stateValue.participantGroupWithParticipants.getOrNull(action.group) ?: return
                val group = groupData.group
                val existingParticipants = groupData.participants
                val nextStartNumber = (existingParticipants.mapNotNull { it.startNumber.toIntOrNull() }.maxOrNull() ?: 0) + 1
                val intervalMs = ((stateValue.competition?.startIntervalSeconds) ?: 60) * 1000L
                val baseTime = stateValue.competition?.startTime ?: 0L
                val startTime = if (baseTime > 0L) baseTime + (nextStartNumber - 1) * intervalMs else 0L
                val participant = OrienteeringParticipant(
                    id = (0..1000L).random(),
                    userId = "",
                    firstName = action.firstName,
                    lastName = action.secondName,
                    groupId = group.groupId,
                    groupName = group.title,
                    competitionId = group.competitionId,
                    commandName = "",
                    startNumber = nextStartNumber.toString(),
                    startTime = startTime,
                    chipNumber = "",
                    comment = "",
                    isChipGiven = false
                )
                viewModelScope.launch(Dispatchers.IO) {
                    val savedParticipant = competitionInteractor.saveParticipant(participant)
                    if (savedParticipant != null) {
                        getCompetitionDetails()
                    }
                }
            }
            is ParticipantListAction.UpdateParticipant -> {
                onAction(ParticipantListAction.HideCreateParticipantDialog)
                viewModelScope.launch(Dispatchers.IO) {
                    competitionInteractor.updateParticipantLocally(action.participant).onSuccess {
                        getCompetitionDetails()
                        competitionInteractor.syncParticipantWithServer(action.participant)
                    }
                }
            }
            ParticipantListAction.HideCreateParticipantDialog -> {
                updateState { copy(isShowParticipantCreateDialog = false, editingParticipant = null) }
            }
        }
    }

    /**
     * Загружает данные соревнования и участников.
     */
    fun getCompetitionDetails() {
        Log.d("LOG_TAG", "getCompetitionDetails: competitionId = $competitionId")
        viewModelScope.launch {
            competitionId?.let { compId ->
                repository.getCompetitionWithDetails(compId).onSuccess {
                    updateState {
                        copy(
                            competition = it.competition,
                            participantGroupWithParticipants = it.groupsWithParticipants
                        )
                    }
                }
            }
        }
    }
}
