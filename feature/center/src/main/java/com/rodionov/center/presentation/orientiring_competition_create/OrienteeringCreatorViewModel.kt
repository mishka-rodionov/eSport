package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.OrienteeringCreatorEffects
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class OrienteeringCreatorViewModel(val navigation: Navigation) : ViewModel() {
    val _state = MutableStateFlow(OrienteeringCreatorState())
    val state: StateFlow<OrienteeringCreatorState> = _state.asStateFlow()

    fun updateState(info: suspend OrienteeringCreatorState.() -> OrienteeringCreatorState) {
        viewModelScope.launch(Dispatchers.Main.immediate) { _state.update { info.invoke(it) } }
    }

    fun onUserAction(action: OrienteeringCreatorEffects) {
        when (action) {
            is OrienteeringCreatorEffects.CreateParticipantGroup -> {
                val isGroupTitleError = action.participantGroup.title.isBlank()
                val isGroupDistanceError = action.participantGroup.distance == 0.0
                val isCountOfControlsError = action.participantGroup.countOfControls == 0
                val isMaxTimeError = action.participantGroup.maxTimeInMinute == 0
                if (isGroupTitleError || isGroupDistanceError || isCountOfControlsError || isMaxTimeError) {
                    updateState {
                        copy(
                            errors = errors.copy(
                                isGroupTitleError = isGroupTitleError,
                                isGroupDistanceError = isGroupDistanceError,
                                isCountOfControlsError = isCountOfControlsError,
                                isMaxTimeError = isMaxTimeError
                            )
                        )
                    }
                } else {
                    if (action.index == -1) {
                        updateState {
                            copy(
                                participantGroups = participantGroups + action.participantGroup,
                                isShowGroupCreateDialog = false
                            )
                        }
                    } else {
                        val groups = _state.value.participantGroups.toMutableList()
                        groups[action.index] = action.participantGroup
                        updateState {
                            copy(
                                participantGroups = groups,
                                isShowGroupCreateDialog = false
                            )
                        }
                    }
                }
            }

            OrienteeringCreatorEffects.Apply -> {
                updateState {
                    copy(
                        errors = errors.copy(
                            isEmptyAddress = address.isBlank(),
                            isEmptyGroup = participantGroups.isEmpty()
                        ),
                        title = if (title.isEmpty()) "Старт ${
                            date.format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            )
                        }" else title
                    )
                }
            }

            is OrienteeringCreatorEffects.UpdateCompetitionDate -> {
                val titleParts = _state.value.title.split(" ")
                val newDate = action.competitionDate.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
                val oldDate = _state.value.date.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
                var newTitle = _state.value.title
                if (titleParts.size == 2 && titleParts[0] == "Старт" && titleParts[1] == oldDate) {
                    newTitle = "Старт $newDate"
                }
                updateState { copy(title = newTitle, date = action.competitionDate) }
            }

            is OrienteeringCreatorEffects.UpdateCompetitionTime -> {
                updateState { copy(time = action.competitionTime) }
            }

            OrienteeringCreatorEffects.ShowGroupCreateDialog -> {
                updateState { copy(isShowGroupCreateDialog = !isShowGroupCreateDialog, editGroupIndex = -1) }
            }

            is OrienteeringCreatorEffects.EditGroupDialog -> {
                updateState { copy(isShowGroupCreateDialog = !isShowGroupCreateDialog, editGroupIndex = action.index) }
            }

            is OrienteeringCreatorEffects.DeleteGroup ->  {
                val group = _state.value.participantGroups.toMutableList()
                group.removeAt(action.index)
                updateState { copy(participantGroups = group.toList()) }
            }
        }
    }
}