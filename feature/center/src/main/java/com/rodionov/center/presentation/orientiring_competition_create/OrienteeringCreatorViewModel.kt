package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrienteeringCreatorViewModel(val navigation: Navigation): ViewModel() {
    val _state = MutableStateFlow(OrienteeringCreatorState())
    val state: StateFlow<OrienteeringCreatorState> = _state.asStateFlow()

    fun updateState(info: suspend OrienteeringCreatorState.() -> OrienteeringCreatorState) {
        viewModelScope.launch(Dispatchers.Main.immediate) { _state.update { info.invoke(it) } }
    }
}