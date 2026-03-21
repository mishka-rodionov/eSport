package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Competition

/**
 * Представляет модель соревнований по спортивному ориентированию
 * 
 * @property competitionId Уникальный идентификатор соревнования
 * @property competition Базовая информация о соревновании
 * @property direction Направление (заданный порядок, по выбору и т.д.)
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим определения времени старта.
 * Строгий - конкретное время,
 * пользовательское - время отсчета до старта,
 * старт по отметке - время старта участника определяется по его отметке на стартовой станции
 * @property countdownTimer Время отсчета перед стартом (в минутах), если [startTimeMode] равен [StartTimeMode.USER_SET]
 * @property startTime Фактическое время старта соревнования (timestamp)
 */
data class OrienteeringCompetition(
    val competitionId: Long,
    val competition: Competition,
    val direction: OrienteeringDirection,
    val punchingSystem: PunchingSystem,
    val startTimeMode: StartTimeMode,
    val countdownTimer: Int? = null,
    val startTime: Long? = null
)
