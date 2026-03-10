package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse

/**
 * Маппер для преобразования ответа сервера в доменную модель соревнования.
 */
fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        title = title,
        date = date, // Теперь оба поля имеют тип Long
        kindOfSport = KindOfSport.fromName(kindOfSport) ?: KindOfSport.Orienteering,
        description = description,
        address = address,
        mainOrganizer = mainOrganizer,
        coordinates = coordinates.toDomain()
    )
}

/**
 * Маппер для преобразования координат из ответа сервера в доменную модель.
 */
fun CoordinatesResponse.toDomain(): Coordinates {
    return Coordinates(latitude, longitude)
}
