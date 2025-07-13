package com.rodionov.domain.models

import java.time.LocalDate

data class Competition(
    val id: String,
    val title: String,
    val date: LocalDate,
    val kindOfSport: KindOfSport,
    val description: String,
    val address: String,
    val coordinates: Coordinates,
)