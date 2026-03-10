package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ParticipantGroup

/**
 * Представляет группу спортивного ориентирования, содержащую информацию о самой группе
 * и список входящих в неё участников с их результатами.
 *
 * @property group Метаданные группы (категории) участников.
 * @property participants Список участников [ParticipantWithResult], включающий их персональные данные и достигнутые результаты.
 */
data class GroupWithParticipantsAndResults(
    val group: ParticipantGroup,
    val participants: List<ParticipantWithResult>
)
