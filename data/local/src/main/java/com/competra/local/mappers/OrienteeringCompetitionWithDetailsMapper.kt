package com.competra.local.mappers

import com.competra.domain.models.orienteering.OrienteeringCompetitionDetails
import com.competra.domain.models.orienteering.ParticipantGroupParticipants
import com.competra.local.entities.orienteering.OrienteeringCompetitionWithDetails
import com.competra.local.entities.orienteering.ParticipantGroupWithParticipants

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
