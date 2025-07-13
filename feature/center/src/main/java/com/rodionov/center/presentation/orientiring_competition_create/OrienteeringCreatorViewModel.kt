package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.OrienteeringCreatorEffects
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.data.navigation.Navigation
import com.rodionov.resources.R
import com.rodionov.resources.ResourceProvider
import com.rodionov.utils.DateTimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrienteeringCreatorViewModel(
    val navigation: Navigation,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
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
                val newDate = DateTimeFormat.formatDate(_state.value.date)
                updateState {
                    copy(
                        errors = errors.copy(
                            isEmptyAddress = address.isBlank(),
                            isEmptyGroup = participantGroups.isEmpty()
                        ),
                        title = if (title.isEmpty()) resourceProvider.getString(
                            R.string.label_competition_start_full,
                            newDate
                        )
                        /*"Старт ${
                            date.format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            )
                        }"*/ else title
                    )
                }
            }

            is OrienteeringCreatorEffects.UpdateCompetitionDate -> {
                val titleParts = _state.value.title.split(" ")
                val newDate = DateTimeFormat.formatDate(action.competitionDate)
                val oldDate = DateTimeFormat.formatDate(_state.value.date)
                var newTitle = _state.value.title
                if (titleParts.size == 2 && titleParts[0] == resourceProvider.getString(R.string.label_competition_start) &&
                    titleParts[1] == oldDate) {
                    newTitle = resourceProvider.getString(R.string.label_competition_start_full, newDate)
                }
                updateState { copy(title = newTitle, date = action.competitionDate) }
            }

            is OrienteeringCreatorEffects.UpdateCompetitionTime -> {
                updateState { copy(time = action.competitionTime) }
            }

            OrienteeringCreatorEffects.ShowGroupCreateDialog -> {
                updateState {
                    copy(
                        isShowGroupCreateDialog = !isShowGroupCreateDialog,
                        editGroupIndex = -1
                    )
                }
            }

            is OrienteeringCreatorEffects.EditGroupDialog -> {
                updateState {
                    copy(
                        isShowGroupCreateDialog = !isShowGroupCreateDialog,
                        editGroupIndex = action.index
                    )
                }
            }

            is OrienteeringCreatorEffects.DeleteGroup -> {
                val group = _state.value.participantGroups.toMutableList()
                group.removeAt(action.index)
                updateState { copy(participantGroups = group.toList()) }
            }
        }
    }
}