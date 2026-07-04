package com.competra.profile.presentation.profile_editor

import android.net.Uri
import com.competra.domain.models.user.User
import com.competra.ui.BaseState

/**
 * Состояние экрана редактирования профиля.
 *
 * @property user Текущие данные пользователя (оригинальные или редактируемые).
 * @property isLoading Флаг загрузки данных пользователя при входе на экран.
 * @property isSaving Флаг процесса сохранения изменений.
 * @property error Сообщение об ошибке, если что-то пошло не так.
 * @property pendingCropUri Uri выбранного, но ещё не подтверждённого в кроппере фото.
 * Пока не null — поверх экрана показан [com.competra.designsystem.components.ImageCropperDialog],
 * а загрузка на сервер не начата.
 */
data class ProfileEditorState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val pendingCropUri: Uri? = null
) : BaseState
