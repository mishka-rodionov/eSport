package com.rodionov.profile.presentation.profile_editor

import com.rodionov.domain.models.user.User
import com.rodionov.ui.BaseState

/**
 * Состояние экрана редактирования профиля.
 *
 * @property user Текущие данные пользователя (оригинальные или редактируемые).
 * @property isLoading Флаг загрузки данных пользователя при входе на экран.
 * @property isSaving Флаг процесса сохранения изменений.
 * @property error Сообщение об ошибке, если что-то пошло не так.
 */
data class ProfileEditorState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
) : BaseState
