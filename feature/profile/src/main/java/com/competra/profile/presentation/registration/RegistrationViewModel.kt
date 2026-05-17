package com.competra.profile.presentation.registration

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.ProfileNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.profile.data.interactors.AuthInteractor
import com.competra.profile.data.registration.RegistrationAction
import com.competra.profile.data.registration.RegistrationState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.DateTimeFormat
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
