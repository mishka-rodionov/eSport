package com.rodionov.profile.presentation.registration

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.profile.data.registration.RegistrationAction
import com.rodionov.profile.data.registration.RegistrationState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.DateTimeFormat
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val navigation: Navigation,
    private val authInteractor: AuthInteractor,
    private val networkErrorRepository: NetworkErrorRepository
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
                    handleFailure(it)
                }
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
