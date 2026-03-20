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
 */
data class EventDetailsState(
    val eventDetails: CyclicEventDetails? = null,
    val isRegistrationSheetVisible: Boolean = false,
    val selectedGroup: EventParticipantGroup? = null,
    val isRegistering: Boolean = false
) : BaseState
