package com.rodionov.center.data

import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

data class OrienteeringCreatorState(
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: String = "12:00",
    val address: String = "",
    val description: String = "",
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val errors: OrienteeringCreatorErrors = OrienteeringCreatorErrors(),
    val isShowGroupCreateDialog: Boolean = false,
    val editGroupIndex: Int = -1
)