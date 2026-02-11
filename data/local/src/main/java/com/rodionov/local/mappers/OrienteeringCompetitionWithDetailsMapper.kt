package com.rodionov.local.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionWithDetails
import com.rodionov.local.entities.orienteering.ParticipantGroupWithParticipants

fun OrienteeringCompetitionWithDetails.toDomain(): OrienteeringCompetitionDetails {
    return OrienteeringCompetitionDetails(
        competition = competition.toDomain(),
        groupsWithParticipants = groupsWithParticipants.map { it.toDomain() }
    )
}

fun ParticipantGroupWithParticipants.toDomain(): ParticipantGroupParticipants {
    return ParticipantGroupParticipants(
        group = group.toDomain(),
        participants = participants.map { it.toDomain() }
    )
}
