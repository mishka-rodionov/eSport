package com.rodionov.center.data.creator

import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.ui.BaseAction

/**
 * Действия пользователя при создании соревнования по ориентированию.
 */
sealed class OrienteeringCreatorAction : BaseAction {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup,
        val index: Int
    ): OrienteeringCreatorAction()

    /**
     * Сохранение созданного соревнования.
     */
    data object Apply: OrienteeringCreatorAction()
    
    /**
     * Обновление даты соревнования.
     * @param competitionDate Дата в миллисекундах.
     */
    data class UpdateCompetitionDate(val competitionDate: Long): OrienteeringCreatorAction()
    
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorAction()
    data object ShowGroupCreateDialog: OrienteeringCreatorAction()
    data class EditGroupDialog(val index: Int): OrienteeringCreatorAction()
    data class DeleteGroup(val index: Int): OrienteeringCreatorAction()
    data class UpdateCompetitionDirection(val direction: OrienteeringDirection): OrienteeringCreatorAction()
    
    /**
     * Обновление режима времени старта.
     * @param startTimeMode Режим времени старта.
     */
    data class UpdateStartTimeMode(val startTimeMode: StartTimeMode): OrienteeringCreatorAction()

    data object SuccessfulCompetitionCreate: OrienteeringCreatorAction()
    data class FailedCompetitionCreate(val message: String): OrienteeringCreatorAction()
}
