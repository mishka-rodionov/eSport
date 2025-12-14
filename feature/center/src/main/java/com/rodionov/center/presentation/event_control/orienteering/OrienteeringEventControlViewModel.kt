package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.launch

class OrienteeringEventControlViewModel(
    private val navigation: Navigation
): BaseViewModel<BaseState>(object : BaseState {}) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {
        when(action) {
            OrientEventControlAction.OpenOrientReadCard -> viewModelScope.launch { navigation.navigate(CenterNavigation.OrientReadCardRoute) }
            OrientEventControlAction.OpenParticipantLists ->  {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.ParticipantList,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }
            OrientEventControlAction.OpenDrawParticipants ->  {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.DrawParticipants,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }
        }
    }

}