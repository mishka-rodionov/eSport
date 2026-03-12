package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.resources.R
import com.rodionov.resources.ResourceProvider
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.DateTimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана создания/редактирования соревнования по ориентированию.
 */
class OrienteeringCreatorViewModel(
    val navigation: Navigation,
    private val resourceProvider: ResourceProvider,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val userRepository: UserRepository
) : BaseViewModel<OrienteeringCreatorState>(OrienteeringCreatorState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is OrienteeringCreatorAction.CreateParticipantGroup -> {
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
                        val groups = stateValue.participantGroups.toMutableList()
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

            OrienteeringCreatorAction.Apply -> handleCompetitionCreate()

            is OrienteeringCreatorAction.UpdateCompetitionDate -> handleCompetitionUpdate(action)

            is OrienteeringCreatorAction.UpdateCompetitionTime -> {
                updateState { copy(time = action.competitionTime) }
            }

            OrienteeringCreatorAction.ShowGroupCreateDialog -> {
                updateState {
                    copy(
                        isShowGroupCreateDialog = !isShowGroupCreateDialog,
                        editGroupIndex = -1
                    )
                }
            }

            is OrienteeringCreatorAction.EditGroupDialog -> {
                updateState {
                    copy(
                        isShowGroupCreateDialog = !isShowGroupCreateDialog,
                        editGroupIndex = action.index
                    )
                }
            }

            is OrienteeringCreatorAction.DeleteGroup -> {
                val group = stateValue.participantGroups.toMutableList()
                group.removeAt(action.index)
                updateState { copy(participantGroups = group.toList()) }
            }

            is OrienteeringCreatorAction.UpdateCompetitionDirection -> {
                updateState { copy(competitionDirection = action.direction) }
            }

            is OrienteeringCreatorAction.UpdateStartTimeMode -> {
                updateState { copy(startTimeMode = action.startTimeMode) }
            }

            OrienteeringCreatorAction.SuccessfulCompetitionCreate -> {}

            is OrienteeringCreatorAction.FailedCompetitionCreate -> {}
        }
    }

    fun initialize(competitionId: Long?) {
        if (competitionId != null) {
            viewModelScope.launch {
                orienteeringCompetitionInteractor.getCompetitionWithDetails(competitionId).onSuccess { details ->
                    updateState {
                        copy(
                            competitionId = competitionId,
                            title = details.competition.competition.title,
                            date = details.competition.competition.date,
                            address = details.competition.competition.address,
                            description = details.competition.competition.description,
                            competitionDirection = details.competition.direction,
                            punchingSystem = details.competition.punchingSystem,
                            participantGroups = details.groupsWithParticipants.map { it.group },
                            startTimeMode = details.competition.startTimeMode
                        )
                    }
                }.onFailure {
                    orienteeringCompetitionInteractor.getCompetition(competitionId)?.let { competition ->
                        updateState {
                            copy(
                                competitionId = competitionId,
                                title = competition.competition.title,
                                date = competition.competition.date,
                                address = competition.competition.address,
                                description = competition.competition.description,
                                competitionDirection = competition.direction,
                                punchingSystem = competition.punchingSystem,
                                startTimeMode = competition.startTimeMode
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleCompetitionUpdate(action: OrienteeringCreatorAction.UpdateCompetitionDate) {
        val titleParts = stateValue.title.split(" ")
        val newDate = DateTimeFormat.transformLongToDisplayDate(action.competitionDate)
        val oldDate = DateTimeFormat.transformLongToDisplayDate(stateValue.date)
        var newTitle = stateValue.title
        if (titleParts.size == 2 && titleParts[0] == resourceProvider.getString(R.string.label_competition_start) &&
            titleParts[1] == oldDate
        ) {
            newTitle =
                resourceProvider.getString(R.string.label_competition_start_full, newDate)
        }
        updateState { copy(title = newTitle, date = action.competitionDate) }
    }

    private fun handleCompetitionCreate() {
        val newDate = DateTimeFormat.transformLongToDisplayDate(stateValue.date)
        updateState {
            copy(
                errors = errors.copy(
                    isEmptyAddress = address.isBlank(),
                    isEmptyGroup = participantGroups.isEmpty()
                ),
                title = title.ifEmpty {
                    resourceProvider.getString(
                        R.string.label_competition_start_full,
                        newDate
                    )
                }
            )
        }
        if (stateValue.errors.checkErrors()) {
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.retrieveUser().onSuccess {
                    val competition = stateValue.constructOrienteeringCompetition(userId = it.id)
                    if (stateValue.competitionId == null) {
                        saveNewCompetition(
                            orienteeringCompetition = competition,
                            participantGroups = stateValue.participantGroups
                        )
                    } else {
                        updateExistingCompetition(
                            orienteeringCompetition = competition,
                            participantGroups = stateValue.participantGroups
                        )
                    }
                }
            }
        }
    }

    private suspend fun saveNewCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ) {
        onAction(
            orienteeringCompetitionInteractor.saveCompetition(
                orienteeringCompetition,
                participantGroups
            )
        )
        navigateToMain()
    }

    private suspend fun updateExistingCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ) {
        onAction(
            orienteeringCompetitionInteractor.updateCompetition(
                orienteeringCompetition,
                participantGroups
            )
        )
        navigateToMain()
    }

    private fun navigateToMain() {
        viewModelScope.launch {
            val destination = CenterNavigation.CenterRoute
            destination.navOptionsBuilder = {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = false
            }
            navigation.navigate(destination = destination)
        }
    }

    fun updateTitle(title: String) = updateState { copy(title = title) }

    fun updateAddress(address: String) = updateState { copy(address = address) }

    fun updateDescription(description: String) = updateState { copy(description = description) }
}
