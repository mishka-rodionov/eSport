package com.rodionov.center.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.navigation.CenterNavigationGraph
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.launch

class CenterViewModel(val navigation: Navigation): ViewModel() {

    fun handleEffects(effect: CenterEffects) {
        if (effect is CenterEffects.OpenKindOfSports) {
            viewModelScope.launch {
                navigation.navigate(CenterNavigationGraph.KindOfSportRoute)
            }
        }
    }

}