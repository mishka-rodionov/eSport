package com.rodionov.center.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.main.CenterState
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CenterViewModel(val navigation: Navigation): ViewModel() {

    private val _state = MutableStateFlow(CenterState())
    val state: StateFlow<CenterState> = _state.asStateFlow()


    fun handleEffects(effect: CenterEffects) {
        when(effect) {
            is CenterEffects.OpenKindOfSports -> {
                viewModelScope.launch {
                    navigation.navigate(CenterNavigation.KindOfSportRoute)
                }
            }

            is CenterEffects.OpenOrienteeringCreator -> viewModelScope.launch {
                navigation.navigate(CenterNavigation.OrienteeringCreatorRoute)
            }

            is CenterEffects.OpenOrienteeringEventControl -> viewModelScope.launch {
                navigation.navigate(CenterNavigation.OrienteeringEventControlRoute)
            }
        }
    }

}