package com.rodionov.profile.presentation.main_profile

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.profile.data.ProfileAction
import com.rodionov.profile.data.ProfileState
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val navigation: Navigation,
    private val userRepository: UserRepository
) : BaseViewModel<ProfileState>(ProfileState()) {

    override fun onAction(action: BaseAction) {

    }

    fun onAction(profileAction: ProfileAction) {
        when (profileAction) {
            ProfileAction.ToAuth -> toAuthorization()
            ProfileAction.ToRegister -> toRegistration()
        }
    }

    fun toRegistration() {
        viewModelScope.launch {
            navigation.navigate(ProfileNavigation.RegistrationRoute)
        }
    }

    fun toAuthorization() {
        viewModelScope.launch {
            navigation.navigate(destination = ProfileNavigation.AuthRoute)
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            userRepository.retrieveUser().onSuccess { user ->
                updateState {
                    copy(user = user)
                }
            }
        }
    }

}