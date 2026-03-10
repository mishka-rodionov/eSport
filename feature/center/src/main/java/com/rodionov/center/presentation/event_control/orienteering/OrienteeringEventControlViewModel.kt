package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.center.data.event_control.OrienteeringEventControlState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана управления соревнованием по ориентированию.
 *
 * @property navigation Навигация приложения.
 * @property orienteeringCompetitionInteractor Интерактор для работы с данными соревнований.
 */
class OrienteeringEventControlViewModel(
    private val navigation: Navigation,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor
) : BaseViewModel<OrienteeringEventControlState>(OrienteeringEventControlState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    init {
        loadCompetitionData()
    }

    private fun loadCompetitionData() {
        val id = competitionId ?: return
        viewModelScope.launch {
            orienteeringCompetitionInteractor.getCompetitionWithDetails(id).onSuccess { details ->
                updateState {
                    copy(
                        competitionTitle = details.competition.competition.title,
                        participantGroups = details.groupsWithParticipants.map { it.group }
                    )
                }
            }.onFailure {
                orienteeringCompetitionInteractor.getCompetition(id)?.let { competition ->
                    updateState {
                        copy(
                            competitionTitle = competition.competition.title
                        )
                    }
                }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            OrientEventControlAction.OpenOrientReadCard -> viewModelScope.launch {
                navigation.navigate(
                    destination = CenterNavigation.OrientReadCardRoute,
                    argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                )
            }

            OrientEventControlAction.OpenParticipantLists -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.ParticipantList,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenDrawParticipants -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.DrawParticipants,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenResults -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.ParticipantResults,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenGetOrienteeringChip -> {
                viewModelScope.launch {
                    competitionId?.let {
                        navigation.navigate(CenterNavigation.GetOrienteeringChipRoute(it))
                    }
                }
            }
        }
    }

}