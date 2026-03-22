package com.rodionov.center.data.creator

import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.ui.BaseAction
import java.time.LocalDate

/**
 * Действия на экране создания соревнования.
 */
sealed class OrienteeringCreatorAction : BaseAction {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup,
        val index: Int
    ): OrienteeringCreatorAction()

    /**
     * Сохранение созданного соревнования
     * */
    data object Apply: OrienteeringCreatorAction()
    data class UpdateCompetitionDate(val competitionDate: Long): OrienteeringCreatorAction()
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorAction()
    data object ShowGroupCreateDialog: OrienteeringCreatorAction()
    data object HideGroupCreateDialog: OrienteeringCreatorAction()
    data class EditGroupDialog(val index: Int): OrienteeringCreatorAction()
    data class DeleteGroup(val index: Int): OrienteeringCreatorAction()
    data class UpdateCompetitionDirection(val direction: OrienteeringDirection): OrienteeringCreatorAction()
    data object SuccessfulCompetitionCreate: OrienteeringCreatorAction()
    data class FailedCompetitionCreate(val message: String): OrienteeringCreatorAction()
    data class UpdateStartTimeMode(val startTimeMode: StartTimeMode): OrienteeringCreatorAction()
    
    // Новые действия для пошагового создания
    data object AddStage : OrienteeringCreatorAction()
    data class RemoveStage(val index: Int) : OrienteeringCreatorAction()
    data class UpdateStageDate(val index: Int, val date: Long) : OrienteeringCreatorAction()
    data class UpdateStageTime(val index: Int, val time: String) : OrienteeringCreatorAction()

    // Действия для работы с дистанциями
    data object ShowDistanceCreateDialog : OrienteeringCreatorAction()
    data object HideDistanceCreateDialog : OrienteeringCreatorAction()
    data class CreateDistance(val distance: Distance, val index: Int) : OrienteeringCreatorAction()
    data class EditDistanceDialog(val index: Int) : OrienteeringCreatorAction()
    data class DeleteDistance(val index: Int) : OrienteeringCreatorAction()
}
