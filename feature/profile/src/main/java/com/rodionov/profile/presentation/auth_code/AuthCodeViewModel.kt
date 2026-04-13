package com.rodionov.profile.presentation.auth_code

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.PendingRegistrationRepository
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.data.navigation.TabRoutes
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.profile.data.auth.AuthAction
import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthCodeViewModel(
    private val authInteractor: AuthInteractor,
    private val navigation: Navigation,
    private val pendingRegistrationRepository: PendingRegistrationRepository,
    private val networkErrorRepository: NetworkErrorRepository
) : BaseViewModel<BaseState>(object : BaseState {}) {

    private var email = ""

    override fun onAction(action: BaseAction) {
        when (action) {
            is AuthAction.AuthCodeEntered -> sendAuthCode(action.code)
            else -> {}
        }
    }

    fun initialize(userEmail: String) {
        email = userEmail
    }

    fun sendAuthCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.authorize(email = email, code = code).onSuccess {
                if (pendingRegistrationRepository.pending.value != null) {
                    navigation.switchTab(TabRoutes.EVENTS)
                } else {
                    navigation.navigate(ProfileNavigation.MainProfileRoute)
                }
            }.onFailure {
                handleFailure(it)
            }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }

}
