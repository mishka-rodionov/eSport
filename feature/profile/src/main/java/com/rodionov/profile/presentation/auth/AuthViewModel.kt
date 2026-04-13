package com.rodionov.profile.presentation.auth

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.auth.AuthAction
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
    private val navigation: Navigation,
    private val authRepository: AuthRepository,
    private val networkErrorRepository: NetworkErrorRepository
) : BaseViewModel<BaseState>(object : BaseState{}) {

    override fun onAction(action: BaseAction) {

        when (action) {
            is AuthAction.AuthClicked -> {
                viewModelScope.launch {
                    authRepository.login(action.email).onSuccess {
                        navigation.navigate(destination = ProfileNavigation.AuthCodeRoute(action.email))
                    }.onFailure {
                        handleFailure(it)
                    }
                }
            }

            else -> {}
        }

    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }

}