package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse
import java.time.Instant
import java.time.ZoneId

fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        title, Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(), KindOfSport.fromName(kindOfSport) ?: KindOfSport.Orienteering, description, address, coordinates.toDomain()
    )
}

fun CoordinatesResponse.toDomain() : Coordinates {
    return Coordinates(latitude, longitude)
}