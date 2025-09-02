package com.rodionov.profile.presentation.auth_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.auth.AuthAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthCodeViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.AuthCodeEntered -> sendAuthCode(action.code)
            else -> {}
        }
    }

    fun sendAuthCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.sendAuthCode(email = "mishka727@yandex.ru" , code = code)
        }
    }

}