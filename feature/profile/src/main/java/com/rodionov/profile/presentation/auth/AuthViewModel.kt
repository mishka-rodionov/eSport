package com.rodionov.profile.presentation.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.profile.data.auth.AuthAction
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.ProfileConstants
import kotlinx.coroutines.launch

class AuthViewModel(
    private val navigation: Navigation,
    private val authRepository: AuthRepository
) : BaseViewModel<BaseState>(object : BaseState{}) {

    override fun onAction(action: BaseAction) {

        when (action) {
            is AuthAction.AuthClicked -> {
                viewModelScope.launch {
                    authRepository.login(action.email).onSuccess {
                        Log.d("LOG_TAG", "onAction: success authorization")
                        navigation.navigate(destination = ProfileNavigation.AuthCodeRoute(action.email))
                    }.onFailure {

                    }
                }
            }

            else -> {}
        }

    }

}