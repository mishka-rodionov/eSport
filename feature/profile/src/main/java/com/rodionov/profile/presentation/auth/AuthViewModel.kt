package com.rodionov.profile.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.auth.AuthAction
import kotlinx.coroutines.launch

class AuthViewModel(
    private val navigation: Navigation,
    private val authRepository: AuthRepository
): ViewModel() {

    fun onAction(action: AuthAction) {

        when(action) {
            is AuthAction.AuthClicked -> {
                viewModelScope.launch {
                    authRepository.requestAuthCode(action.email).onSuccess {
                        Log.d("LOG_TAG", "onAction: success authorization")
                        navigation.navigate(destination = ProfileNavigation.AuthCodeRoute, argument = listOf() )
                    }.onFailure {

                    }
                }
            }
            else -> {}
        }

    }

}