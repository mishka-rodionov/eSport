package com.rodionov.profile.presentation.profile_editor

import androidx.lifecycle.viewModelScope
import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана редактирования профиля.
 * Управляет загрузкой, редактированием и сохранением данных пользователя.
 *
 * @param userRepository Репозиторий для работы с данными пользователя.
 */
class ProfileEditorViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<ProfileEditorState>(ProfileEditorState()) {

    init {
        loadUser()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is ProfileEditorAction.UpdateFirstName -> updateFirstName(action.firstName)
            is ProfileEditorAction.UpdateLastName -> updateLastName(action.lastName)
            is ProfileEditorAction.UpdateMiddleName -> updateMiddleName(action.middleName)
            is ProfileEditorAction.UpdatePhoneNumber -> updatePhoneNumber(action.phoneNumber)
            is ProfileEditorAction.UpdateEmail -> updateEmail(action.email)
            is ProfileEditorAction.SaveProfile -> saveProfile()
        }
    }

    /**
     * Загружает информацию о текущем пользователе.
     */
    private fun loadUser() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.retrieveUser()
                .onSuccess { user ->
                    updateState { copy(user = user, isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false, error = "Ошибка загрузки данных") }
                }
        }
    }

    private fun updateFirstName(firstName: String) {
        updateState { copy(user = user?.copy(firstName = firstName)) }
    }

    private fun updateLastName(lastName: String) {
        updateState { copy(user = user?.copy(lastName = lastName)) }
    }

    private fun updateMiddleName(middleName: String) {
        updateState { copy(user = user?.copy(middleName = middleName)) }
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        updateState { copy(user = user?.copy(phoneNumber = phoneNumber)) }
    }

    private fun updateEmail(email: String) {
        updateState { copy(user = user?.copy(email = email)) }
    }

    /**
     * Сохраняет изменения профиля (локально и на сервере).
     */
    private fun saveProfile() {
        val userToSave = stateValue.user ?: return
        updateState { copy(isSaving = true) }
        viewModelScope.launch {
            // Имитация задержки сетевого запроса (моки)
            delay(1500)
            
            userRepository.saveUser(userToSave)
                .onSuccess {
                    updateState { copy(isSaving = false) }
                    // TODO: Навигация назад или показ сообщения об успехе
                }
                .onFailure {
                    updateState { copy(isSaving = false, error = "Ошибка сохранения") }
                }
        }
    }
}

/**
 * Действия на экране редактирования профиля.
 */
sealed interface ProfileEditorAction : BaseAction {
    data class UpdateFirstName(val firstName: String) : ProfileEditorAction
    data class UpdateLastName(val lastName: String) : ProfileEditorAction
    data class UpdateMiddleName(val middleName: String) : ProfileEditorAction
    data class UpdatePhoneNumber(val phoneNumber: String) : ProfileEditorAction
    data class UpdateEmail(val email: String) : ProfileEditorAction
    data object SaveProfile : ProfileEditorAction
}
