package com.rodionov.center.data.main

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import java.time.LocalDate

data class CenterState(
//    val controlledEvents: List<Competition> = emptyList()
    val controlledEvents: List<Competition> = mockEvents()
)

fun mockEvents(): List<Competition> {
    return listOf(
        Competition(
            title = "Городские соревнования",
            date = LocalDate.parse("2025-08-25"),
            kindOfSport = KindOfSport.Orienteering,
            description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
            address = "Саратов",
            coordinates = Coordinates(
                latitude = 51.3200,
                longitude = 46.0000
            )
        ),
        Competition(
            title = "Городские соревнования #2",
            date = 	LocalDate.parse("2025-08-27"),
            kindOfSport = KindOfSport.Orienteering,
            description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
            address = "Балашов",
            coordinates = Coordinates(
                latitude = 51.3200,
                longitude = 46.0000
            )
        )
    )
}