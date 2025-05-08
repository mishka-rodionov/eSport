package com.rodionov.sportsenthusiast.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun collectNavigationEffect(navigationHandler: (BaseNavigation) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            navigation.collectNavigationEffect(navigationHandler)
        }
    }

    fun navigate() {

    }

}