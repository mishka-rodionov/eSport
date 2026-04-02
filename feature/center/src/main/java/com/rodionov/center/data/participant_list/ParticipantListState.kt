package com.rodionov.center.data.participant_list

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import com.rodionov.ui.BaseState

/**
 * Состояние экрана списка участников.
 *
 * @property participantGroupWithParticipants Список групп с участниками.
 * @property competition Данные текущего соревнования (для расчёта стартового времени).
 * @property isShowParticipantCreateDialog Флаг отображения диалога создания/редактирования.
 * @property group Индекс текущей выбранной группы.
 * @property editingParticipant Участник, который редактируется в данный момент (null для создания).
 */
data class ParticipantListState(
    val participantGroupWithParticipants: List<ParticipantGroupParticipants> = emptyList(),
    val competition: OrienteeringCompetition? = null,
    val isShowParticipantCreateDialog: Boolean = false,
    val group: Int = 0,
    val editingParticipant: OrienteeringParticipant? = null,
    val deletingParticipant: OrienteeringParticipant? = null
): BaseState
