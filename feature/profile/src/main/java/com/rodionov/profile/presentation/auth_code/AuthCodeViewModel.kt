package com.rodionov.profile.presentation.auth_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.auth.AuthAction
import com.rodionov.utils.constants.ProfileConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthCodeViewModel(
    private val authRepository: AuthRepository,
    private val navigation: Navigation
): ViewModel() {

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.AuthCodeEntered -> sendAuthCode(action.code)
            else -> {}
        }
    }

    fun sendAuthCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            navigation.getArguments<String>(ProfileConstants.AUTH_EMAIL.name)?.let { email ->
                authRepository.sendAuthCode(email = email, code = code)
            }
        }
    }

}