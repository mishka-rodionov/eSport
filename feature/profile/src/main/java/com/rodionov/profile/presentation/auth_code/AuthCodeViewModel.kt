package com.rodionov.profile.presentation.auth_code

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.PendingRegistrationRepository
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.data.navigation.TabRoutes
import com.rodionov.data.navigation.getArguments
import com.rodionov.profile.data.auth.AuthAction
import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.ProfileConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthCodeViewModel(
    private val authInteractor: AuthInteractor,
    private val navigation: Navigation,
    private val pendingRegistrationRepository: PendingRegistrationRepository
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
                Log.d("LOG_TAG", "sendAuthCode: $it")
            }
        }
    }

}
