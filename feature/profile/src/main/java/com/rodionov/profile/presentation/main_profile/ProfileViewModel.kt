package com.rodionov.profile.presentation.main_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun toAuthorization() {
        viewModelScope.launch {
            navigation.navigate(destination = ProfileNavigation.AuthRoute)
        }
    }

}