package com.rodionov.center.data.get_chip

import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import com.rodionov.ui.BaseState

/**
 * Состояние экрана выдачи чипов.
 *
 * @property groupsWithParticipants Список групп с их участниками.
 * @property isLoading Флаг загрузки данных.
 * @property isSaving Флаг процесса сохранения данных.
 */
data class GetOrienteeringChipState(
    val groupsWithParticipants: List<ParticipantGroupParticipants> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
) : BaseState