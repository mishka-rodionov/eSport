package com.rodionov.events.data.event_participant_group

import com.rodionov.domain.models.Participant
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.ui.BaseState

/**
 * Состояние экрана группы участников события.
 * @property eventId Идентификатор события.
 * @property participantGroup Данные о группе участников.
 * @property participants Список участников группы.
 * @property isLoading Флаг загрузки данных.
 * @property isUserRegistered Зарегистрирован ли текущий пользователь в этой группе.
 * @property isRegistering Флаг процесса регистрации/отмены регистрации.
 */
data class EventParticipantGroupState(
    val eventId: String? = null,
    val participantGroup: EventParticipantGroup? = null,
    val participants: List<OrienteeringParticipant> = emptyList(),
    val isLoading: Boolean = false,
    val isUserRegistered: Boolean = false,
    val isRegistering: Boolean = false
) : BaseState
