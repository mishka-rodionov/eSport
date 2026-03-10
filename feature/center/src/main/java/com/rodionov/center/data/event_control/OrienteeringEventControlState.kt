package com.rodionov.center.data.event_control

import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.ui.BaseState

/**
 * Состояние экрана управления соревнованием по ориентированию.
 *
 * @property participantGroups Список групп участников соревнования.
 * @property competitionTitle Название соревнования.
 */
data class OrienteeringEventControlState(
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val competitionTitle: String = ""
) : BaseState