package com.rodionov.center.data

import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

sealed class OrienteeringCreatorEffects {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup,
        val index: Int
    ): OrienteeringCreatorEffects()

    /**
     * Сохранение созданного соревнования
     * */
    data object Apply: OrienteeringCreatorEffects()
    data class UpdateCompetitionDate(val competitionDate: LocalDate): OrienteeringCreatorEffects()
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorEffects()
    data object ShowGroupCreateDialog: OrienteeringCreatorEffects()
    data class EditGroupDialog(val index: Int): OrienteeringCreatorEffects()
    data class DeleteGroup(val index: Int): OrienteeringCreatorEffects()
    data class UpdateCompetitionDirection(val direction: OrienteeringDirection): OrienteeringCreatorEffects()

}