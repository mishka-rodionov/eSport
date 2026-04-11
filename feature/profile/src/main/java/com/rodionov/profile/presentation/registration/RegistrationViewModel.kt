package com.rodionov.profile.presentation.registration

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.profile.data.registration.RegistrationAction
import com.rodionov.profile.data.registration.RegistrationState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.DateTimeFormat
import com.rodionov.utils.constants.ProfileConstants
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val navigation: Navigation,
    private val authInteractor: AuthInteractor
) : BaseViewModel<RegistrationState>(RegistrationState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            RegistrationAction.RegisterUser -> registerUser()
            is RegistrationAction.UpdateEmail -> updateState { copy(email = action.email) }
            is RegistrationAction.UpdateFirstName -> updateState { copy(firstName = action.firstName) }
            is RegistrationAction.UpdateLastName -> updateState { copy(lastName = action.lastName) }
            is RegistrationAction.UpdateBdate -> updateState { copy(bdate = DateTimeFormat.transformApiDateToLong(action.bdate)) }
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            with(state.value) {
                authInteractor.register(
                    firstName = firstName,
                    lastName = lastName,
                    bdate = bdate,
                    email = email
                ).onSuccess {
                    navigation.navigate(destination = ProfileNavigation.AuthCodeRoute(email))
                }.onFailure {
                    Log.d("LOG_TAG", "registerUser: fail user register")
                }
            }
        }
    }

}