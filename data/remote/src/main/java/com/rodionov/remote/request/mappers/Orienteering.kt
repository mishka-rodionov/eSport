package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest

fun OrienteeringCompetition.toRequest(): OrienteeringCompetitionRequest {
    return OrienteeringCompetitionRequest(
        competitionId,
        competition.toRequest(),
        direction.name
    )
}

fun ParticipantGroup.toRequest(compId: Long? = null): ParticipantGroupRequest {
    return ParticipantGroupRequest(
        groupId, compId ?: competitionId, title, distance, countOfControls, maxTimeInMinute
    )
}