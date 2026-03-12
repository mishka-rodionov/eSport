package com.rodionov.center.data.event_control

import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.ui.BaseState

/**
 * Состояние экрана управления соревнованием по ориентированию.
 *
 * @property participantGroups Список групп участников соревнования.
 * @property competitionTitle Название соревнования.
 * @property competition Данные соревнования.
 * @property countdownMillis Оставшееся время до старта в миллисекундах.
 * @property isTimerRunning Флаг, запущен ли таймер отсчета.
 */
data class OrienteeringEventControlState(
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val competitionTitle: String = "",
    val competition: OrienteeringCompetition? = null,
    val countdownMillis: Long = 0L,
    val isTimerRunning: Boolean = false
) : BaseState