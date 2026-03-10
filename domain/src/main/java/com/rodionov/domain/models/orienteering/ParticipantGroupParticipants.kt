package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ParticipantGroup

/**
 * Представляет связь между группой ориентирования и списком участников, входящих в эту группу.
 *
 * @property group Группа (категория) ориентирования.
 * @property participants Список участников, относящихся к данной группе.
 */
data class ParticipantGroupParticipants(
    val group: ParticipantGroup,
    val participants: List<OrienteeringParticipant>
)
