package com.rodionov.sportsenthusiast.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.sportsenthusiast.BottomNavItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun collectNavigationEffect(navigationHandler: (BaseNavigation) -> Unit, destination: BaseNavigation) {
        viewModelScope.launch(Dispatchers.Main) {
            navigation.collectNavigationEffect(navigationHandler, destination)
        }
    }

    fun navigate() {

    }

}