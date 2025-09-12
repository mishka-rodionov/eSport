package com.rodionov.profile.presentation.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.RegistrationAction
import com.rodionov.utils.constants.ProfileConstants
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val navigation: Navigation,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun onAction(action: RegistrationAction) {
        when (action) {
            is RegistrationAction.RegisterUser -> registerUser(action)
        }
    }

    fun registerUser(action: RegistrationAction.RegisterUser) {
        viewModelScope.launch {
            with(action) {
                authRepository.register(
                    firstName = firstName,
                    lastName = lastName,
                    bdate = bdate,
                    email = email
                ).onSuccess {
                    navigation.navigate(
                        destination = ProfileNavigation.AuthCodeRoute,
                        argument = navigation.createArguments(ProfileConstants.AUTH_EMAIL.name to action.email)
                    )
                }.onFailure {
                    Log.d("LOG_TAG", "registerUser: fail user register")
                }
            }
        }
    }

}