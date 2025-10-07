package com.rodionov.domain.models

import androidx.room.Embedded
import java.time.LocalDate

data class Competition(
    val title: String,
    val date: LocalDate,
    val kindOfSport: KindOfSport,
    val description: String,
    val address: String,
    val mainOrganizer: String,
    @Embedded
    val coordinates: Coordinates,
)