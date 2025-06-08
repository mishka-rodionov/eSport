package com.rodionov.center.data

import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

data class OrienteeringCreatorState(
    val title: String = "",
    val date: LocalDate = LocalDate.of(1970, 1, 1),
    val address: String = "",
    val description: String = "",
    val participantGroups: List<ParticipantGroup> = emptyList()
)