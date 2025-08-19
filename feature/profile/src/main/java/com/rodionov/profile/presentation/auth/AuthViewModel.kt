package com.rodionov.profile.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.profile.data.auth.AuthAction
import kotlinx.coroutines.launch

class AuthViewModel(
    private val navigation: Navigation
): ViewModel() {

    fun onAction(action: AuthAction) {

        when(action) {
            AuthAction.AuthClicked -> {
                viewModelScope.launch {
                    navigation.navigate(destination = ProfileNavigation.AuthCodeRoute)
                }
            }
            else -> {}
        }

    }

}