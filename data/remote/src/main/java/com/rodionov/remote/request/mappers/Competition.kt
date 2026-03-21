package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.remote.request.competition.CompetitionRequest
import com.rodionov.remote.request.competition.CoordinatesRequest

/**
 * Преобразует доменную модель Competition в модель запроса CompetitionRequest.
 */
fun Competition.toRequest(): CompetitionRequest {
    return CompetitionRequest(
        remoteId = remoteId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        kindOfSport = kindOfSport.name,
        description = description,
        address = address,
        mainOrganizerId = mainOrganizerId,
        coordinates = coordinates?.toRequest(),
        status = status.name,
        registrationStart = registrationStart,
        registrationEnd = registrationEnd,
        maxParticipants = maxParticipants,
        feeAmount = feeAmount,
        feeCurrency = feeCurrency,
        regulationUrl = regulationUrl,
        mapUrl = mapUrl,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        website = website,
        resultsStatus = resultsStatus.name
    )
}

/**
 * Преобразует доменную модель Coordinates в модель запроса CoordinatesRequest.
 */
fun Coordinates.toRequest(): CoordinatesRequest {
    return CoordinatesRequest(
        latitude = latitude,
        longitude = longitude
    )
}
