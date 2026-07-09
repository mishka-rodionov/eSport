package com.competra.eventdetails.data.details

import com.competra.domain.models.cyclic_event.CyclicEventDetails
import com.competra.domain.models.cyclic_event.EventParticipantGroup
import com.competra.ui.BaseState

/**
 * Состояние экрана деталей события.
 *
 * @param eventDetails Детали события.
 * @param isRegistrationSheetVisible Видимость BottomSheet регистрации.
 * @param selectedGroup Выбранная группа для регистрации.
 * @param isRegistering Флаг процесса регистрации (загрузка).
 * @param isUserRegistered Флаг того, зарегистрирован ли пользователь на это событие.
 * @param commandName Название клуба/команды участника (свободный текст, опционально).
 */
data class EventDetailsState(
    val eventDetails: CyclicEventDetails? = null,
    val isRegistrationSheetVisible: Boolean = false,
    val selectedGroup: EventParticipantGroup? = null,
    val isRegistering: Boolean = false,
    val isUserRegistered: Boolean = false,
    val commandName: String = "",
    val error: String? = null
) : BaseState
