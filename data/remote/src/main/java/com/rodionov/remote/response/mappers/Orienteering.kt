package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse

fun OrienteeringCompetitionResponse.toDomain(): OrienteeringCompetition {
    return OrienteeringCompetition(
        competitionId, competition.toDomain(), OrienteeringDirection.valueOf(direction)
    )
}

fun ParticipantGroupResponse.toDomain() : ParticipantGroup {
    return ParticipantGroup(groupId, competitionId, title, distance, countOfControls, maxTimeInMinute)
}