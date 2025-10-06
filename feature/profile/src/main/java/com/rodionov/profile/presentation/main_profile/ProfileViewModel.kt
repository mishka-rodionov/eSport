package com.rodionov.profile.presentation.main_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.profile.data.ProfileAction
import com.rodionov.profile.data.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val navigation: Navigation,
    private val userRepository: UserRepository
): ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun onAction(profileAction: ProfileAction) {
        when(profileAction) {
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
                _state.update {
                    it.copy(user = user)
                }
            }
        }
    }

}