package com.rodionov.profile.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.profile.data.RegistrationAction
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun onAction(action: RegistrationAction) {
        when(action) {
            is RegistrationAction.RegisterUser -> registerUser()
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            navigation.navigate(ProfileNavigation.AuthRoute)
        }
    }

}