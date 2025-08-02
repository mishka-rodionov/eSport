package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse

fun OrienteeringCompetitionResponse.toDomain(): OrienteeringCompetition {
    return OrienteeringCompetition(
        competitionId, competition, OrienteeringDirection.valueOf(direction)
    )
}