package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class OrienteeringEventControlViewModel(
    private val navigation: Navigation
): BaseViewModel<BaseState>(object : BaseState {}) {

    override fun onAction(action: BaseAction) {

    }

    fun onAction(action: OrientEventControlAction) {
        when(action) {
            OrientEventControlAction.OpenOrientReadCard -> viewModelScope.launch { navigation.navigate(CenterNavigation.OrientReadCardRoute) }
            OrientEventControlAction.OpenParticipantLists ->  {}
        }
    }

}