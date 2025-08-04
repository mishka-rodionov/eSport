package com.rodionov.center.data

import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

sealed class OrienteeringCreatorAction {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup,
        val index: Int
    ): OrienteeringCreatorAction()

    /**
     * Сохранение созданного соревнования
     * */
    data object Apply: OrienteeringCreatorAction()
    data class UpdateCompetitionDate(val competitionDate: LocalDate): OrienteeringCreatorAction()
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorAction()
    data object ShowGroupCreateDialog: OrienteeringCreatorAction()
    data class EditGroupDialog(val index: Int): OrienteeringCreatorAction()
    data class DeleteGroup(val index: Int): OrienteeringCreatorAction()
    data class UpdateCompetitionDirection(val direction: OrienteeringDirection): OrienteeringCreatorAction()
    data object SuccessfulCompetitionCreate: OrienteeringCreatorAction()
    data class FailedCompetitionCreate(val message: String): OrienteeringCreatorAction()
}