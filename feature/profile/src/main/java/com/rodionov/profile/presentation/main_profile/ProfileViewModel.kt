package com.rodionov.profile.presentation.main_profile

import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.profile.data.ProfileAction
import com.rodionov.profile.data.ProfileState
import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана профиля.
 * Управляет получением данных пользователя и навигацией.
 *
 * @param navigation Сервис навигации.
 * @param userRepository Репозиторий для работы с данными пользователя.
 * @param authInteractor Интерактор для операций аутентификации.
 */
class ProfileViewModel(
    private val navigation: Navigation,
    private val userRepository: UserRepository,
    private val authInteractor: AuthInteractor
) : BaseViewModel<ProfileState>(ProfileState()) {

    override fun onAction(action: BaseAction) {

    }

    /**
     * Обработка действий пользователя на экране профиля.
     */
    fun onAction(profileAction: ProfileAction) {
        when (profileAction) {
            ProfileAction.ToAuth -> toAuthorization()
            ProfileAction.ToRegister -> toRegistration()
            ProfileAction.ToProfileEditor -> toProfileEditor()
            ProfileAction.Logout -> logout()
        }
    }

    /**
     * Переход на экран регистрации.
     */
    private fun toRegistration() {
        viewModelScope.launch {
            navigation.navigate(ProfileNavigation.RegistrationRoute)
        }
    }

    /**
     * Переход на экран авторизации.
     */
    private fun toAuthorization() {
        viewModelScope.launch {
            navigation.navigate(destination = ProfileNavigation.AuthRoute)
        }
    }

    /**
     * Переход на экран редактирования профиля.
     */
    private fun toProfileEditor() {
        viewModelScope.launch {
            navigation.navigate(ProfileNavigation.ProfileEditorRoute)
        }
    }

    /**
     * Выполняет выход из аккаунта: очищает токены и данные пользователя.
     */
    private fun logout() {
        viewModelScope.launch {
            authInteractor.logout()
            updateState { copy(user = null) }
        }
    }

    /**
     * Загружает данные текущего авторизованного пользователя.
     */
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
