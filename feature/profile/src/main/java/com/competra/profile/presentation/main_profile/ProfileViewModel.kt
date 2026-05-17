package com.competra.profile.presentation.main_profile

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.ProfileNavigation
import com.competra.domain.repository.user.UserRepository
import com.competra.profile.data.ProfileAction
import com.competra.profile.data.ProfileState
import com.competra.profile.data.interactors.AuthInteractor
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
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
            ProfileAction.ToUserRegistrations -> openUserRegistrations()
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
     * Переход на экран «Предстоящие старты» — список соревнований пользователя.
     */
    private fun openUserRegistrations() {
        viewModelScope.launch {
            navigation.navigate(ProfileNavigation.UserRegistrationsRoute)
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
