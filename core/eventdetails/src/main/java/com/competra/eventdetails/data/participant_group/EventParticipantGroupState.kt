package com.competra.eventdetails.data.participant_group

import com.competra.domain.models.Participant
import com.competra.domain.models.cyclic_event.EventParticipantGroup
import com.competra.domain.models.events.EventStatus
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.ui.BaseState

/**
 * Состояние экрана группы участников события.
 * @property eventId Идентификатор события.
 * @property participantGroup Данные о группе участников.
 * @property participants Список участников группы.
 * @property isLoading Флаг загрузки данных.
 * @property isUserRegistered Зарегистрирован ли текущий пользователь в этой группе.
 * @property isRegistering Флаг процесса регистрации/отмены регистрации.
 * @property eventStatus Текущий статус события. Кнопка регистрации показывается только при [EventStatus.REGISTRATION].
 */
data class EventParticipantGroupState(
    val eventId: Long? = null,
    val participantGroup: EventParticipantGroup? = null,
    val participants: List<OrienteeringParticipant> = emptyList(),
    val isLoading: Boolean = false,
    val isUserRegistered: Boolean = false,
    val isRegistering: Boolean = false,
    val eventStatus: EventStatus? = null
) : BaseState
