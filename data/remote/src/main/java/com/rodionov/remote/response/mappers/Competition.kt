package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.ResultsStatus
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse

/**
 * Маппер для преобразования ответа сервера в доменную модель соревнования.
 */
fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        remoteId = remoteId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        kindOfSport = KindOfSport.fromName(kindOfSport) ?: KindOfSport.Orienteering,
        description = description,
        address = address,
        mainOrganizerId = mainOrganizerId,
        coordinates = coordinates?.toDomain(),
        status = try {
            CompetitionStatus.valueOf(status)
        } catch (e: Exception) {
            CompetitionStatus.UNKNOWN
        },
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
        resultsStatus = try {
            ResultsStatus.valueOf(resultsStatus)
        } catch (e: Exception) {
            ResultsStatus.NOT_PUBLISHED
        },
        // Данные получены с сервера, помечаем как синхронизированные
        isSynced = true,
        lastModified = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis()
    )
}

/**
 * Маппер для преобразования координат из ответа сервера в доменную модель.
 */
fun CoordinatesResponse.toDomain(): Coordinates? {
    return Coordinates(latitude, longitude)
}
