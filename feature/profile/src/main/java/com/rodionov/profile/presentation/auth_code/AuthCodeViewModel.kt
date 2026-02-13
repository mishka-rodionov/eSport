package com.rodionov.profile.presentation.auth_code

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
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
    private val navigation: Navigation
) : BaseViewModel<BaseState>(object : BaseState {}) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is AuthAction.AuthCodeEntered -> sendAuthCode(action.code)
            else -> {}
        }
    }

    fun sendAuthCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            navigation.getArguments<String>(ProfileConstants.AUTH_EMAIL.name)?.let { email ->
                authInteractor.authorize(email = email, code = code).onSuccess {
                    navigation.navigate(ProfileNavigation.MainProfileRoute)
                }.onFailure {
                    Log.d("LOG_TAG", "sendAuthCode: $it")
                }
            }
        }
    }

}