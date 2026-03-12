package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Competition

/**
 * Представляет модель соревнований по спортивному ориентированию
 * 
 * @property competitionId Уникальный идентификатор соревнования
 * @property competition Базовая информация о соревновании
 * @property direction Направление (заданный порядок, по выбору и т.д.)
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим определения времени старта
 */
data class OrienteeringCompetition(
    val competitionId: Long,
    val competition: Competition,
    val direction: OrienteeringDirection,
    val punchingSystem: PunchingSystem,
    val startTimeMode: StartTimeMode
)
