package com.rodionov.center.presentation.get_chip

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.get_chip.GetOrienteeringChipAction
import com.rodionov.center.data.get_chip.GetOrienteeringChipState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана выдачи чипов участникам.
 *
 * @property orienteeringCompetitionInteractor Интерактор для работы с данными соревнований.
 */
class GetOrienteeringChipViewModel(
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor
) : BaseViewModel<GetOrienteeringChipState>(GetOrienteeringChipState()) {

    /**
     * Загружает группы участников для указанного соревнования.
     * @param competitionId ID соревнования.
     */
    fun loadParticipants(competitionId: Long) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            orienteeringCompetitionInteractor.getCompetitionWithDetails(competitionId).onSuccess { details ->
                updateState {
                    copy(
                        groupsWithParticipants = details.groupsWithParticipants,
                        isLoading = false
                    )
                }
            }.onFailure {
                updateState { copy(isLoading = false) }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is GetOrienteeringChipAction.UpdateChipNumber -> {
                updateParticipantInState(action.participantId) {
                    it.copy(chipNumber = action.chipNumber)
                }
            }

            is GetOrienteeringChipAction.ToggleChipGiven -> {
                // В данной реализации чекбокс может использоваться как визуальный индикатор выдачи.
            }

            GetOrienteeringChipAction.SaveChanges -> {
                saveChanges()
            }
        }
    }

    private fun updateParticipantInState(
        participantId: String,
        update: (OrienteeringParticipant) -> OrienteeringParticipant
    ) {
        val updatedGroups = stateValue.groupsWithParticipants.map { group ->
            group.copy(
                participants = group.participants.map { participant ->
                    if (participant.userId == participantId) update(participant) else participant
                }
            )
        }
        updateState { copy(groupsWithParticipants = updatedGroups) }
    }

    private fun saveChanges() {
        updateState { copy(isSaving = true) }
        viewModelScope.launch {
            val allParticipants = stateValue.groupsWithParticipants.flatMap { it.participants }
            orienteeringCompetitionInteractor.updateParticipants(allParticipants)
            updateState { copy(isSaving = false) }
        }
    }
}
