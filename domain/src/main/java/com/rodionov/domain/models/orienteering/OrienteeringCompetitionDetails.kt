package com.rodionov.domain.models.orienteering

/**
 * Представляет подробную информацию о соревнованиях по ориентированию, объединяющую
 * основные данные о событии и списки участников, распределенных по группам.
 *
 * @property competition Основная информация и настройки соревнования.
 * @property groupsWithParticipants Список групп (категорий) с относящимися к ним участниками.
 */
data class OrienteeringCompetitionDetails(
    val competition: OrienteeringCompetition,
    val groupsWithParticipants: List<ParticipantGroupParticipants>
)
