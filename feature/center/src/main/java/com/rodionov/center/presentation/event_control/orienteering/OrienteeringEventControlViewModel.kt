package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.launch

class OrienteeringEventControlViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun onAction(action: OrientEventControlAction) {
        when(action) {
            OrientEventControlAction.OpenOrientReadCard -> viewModelScope.launch { navigation.navigate(CenterNavigation.OrientReadCardRoute) }
        }
    }

}