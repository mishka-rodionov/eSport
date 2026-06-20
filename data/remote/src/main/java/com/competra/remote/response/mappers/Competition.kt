package com.competra.remote.response.mappers

import com.competra.domain.models.Competition
import com.competra.domain.models.Coordinates
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.orienteering.CompetitionStatus
import com.competra.domain.models.orienteering.ResultsStatus
import com.competra.remote.response.competition.CompetitionResponse
import com.competra.remote.response.competition.CoordinatesResponse

/**
 * Маппер для преобразования ответа сервера в доменную модель соревнования.
 */
fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        id = id,
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
        imageUrl = imageUrl,
        regulationUrl = regulationUrl,
        mapUrl = mapUrl,
        resultsUrl = resultsUrl,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        website = website,
        resultsStatus = try {
            ResultsStatus.valueOf(resultsStatus)
        } catch (e: Exception) {
            ResultsStatus.NOT_PUBLISHED
        },
        timeZoneId = timeZoneId ?: "UTC",
        // Данные получены с сервера, помечаем как синхронизированные
        isSynced = true,
        lastModified = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis(),
        serverUpdatedAt = updatedAt.takeIf { it > 0L }
    )
}

/**
 * Маппер для преобразования координат из ответа сервера в доменную модель.
 */
fun CoordinatesResponse.toDomain(): Coordinates {
    return Coordinates(latitude, longitude)
}
