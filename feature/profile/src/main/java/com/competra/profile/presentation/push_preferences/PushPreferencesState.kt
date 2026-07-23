package com.competra.profile.presentation.push_preferences

import com.competra.ui.BaseState

/**
 * Состояние экрана настроек push-уведомлений.
 *
 * @property resultsPublished Уведомлять о публикации результатов соревнования.
 * @property competitionStart Уведомлять о скором старте соревнования.
 * @property dayBeforeReminder Уведомлять за сутки до старта соревнования, на которое зарегистрирован пользователь.
 * @property isLoading Флаг загрузки текущих значений из хранилища.
 */
data class PushPreferencesState(
    val resultsPublished: Boolean = true,
    val competitionStart: Boolean = true,
    val dayBeforeReminder: Boolean = true,
    val isLoading: Boolean = true,
) : BaseState
