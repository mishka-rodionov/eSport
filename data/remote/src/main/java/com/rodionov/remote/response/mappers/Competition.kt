package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse

fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        title, date, kindOfSport, description, address, coordinates.toDomain()
    )
}

fun CoordinatesResponse.toDomain() : Coordinates {
    return Coordinates(latitude, longitude)
}