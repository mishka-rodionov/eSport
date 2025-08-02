package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest

fun OrienteeringCompetition.toRequest(): OrienteeringCompetitionRequest {
    return OrienteeringCompetitionRequest(
        competitionId,
        competition.toRequest(),
        direction.name
    )
}