package com.rodionov.events.data.details

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.ui.BaseState

/**
 * Состояние экрана деталей события.
 *
 * @param eventDetails Детали события.
 * @param isRegistrationSheetVisible Видимость BottomSheet регистрации.
 * @param selectedGroup Выбранная группа для регистрации.
 * @param isRegistering Флаг процесса регистрации (загрузка).
 * @param isUserRegistered Флаг того, зарегистрирован ли пользователь на это событие.
 */
data class EventDetailsState(
    val eventDetails: CyclicEventDetails? = null,
    val isRegistrationSheetVisible: Boolean = false,
    val selectedGroup: EventParticipantGroup? = null,
    val isRegistering: Boolean = false,
    val isUserRegistered: Boolean = false,
    val error: String? = null
) : BaseState
