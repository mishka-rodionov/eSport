package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.remote.request.competition.CompetitionRequest
import com.rodionov.remote.request.competition.CoordinatesRequest

fun Competition.toRequest(): CompetitionRequest {
    return CompetitionRequest(
        title, date.toEpochDay() * 86400, kindOfSport.name, description, address, coordinates.toRequest()
    )
}

fun Coordinates.toRequest(): CoordinatesRequest {
    return CoordinatesRequest(
        latitude, longitude
    )
}