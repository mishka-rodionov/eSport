package com.competra.profile.presentation.auth_code

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.PendingRegistrationRepository
import com.competra.data.navigation.ProfileNavigation
import com.competra.data.navigation.TabRoutes
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.profile.data.auth.AuthAction
import com.competra.profile.data.interactors.AuthInteractor
import com.competra.ui.BaseAction
import com.competra.ui.BaseState
import com.competra.ui.viewmodel.BaseViewModel
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
