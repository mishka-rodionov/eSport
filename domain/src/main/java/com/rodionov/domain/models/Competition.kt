package com.rodionov.domain.models

import java.time.LocalDate

data class Competition(
    val id: String,
    val title: String,
    val date: LocalDate,
    val kindOfSport: KindOfSport,
    val description: String,
    val participants: List<Participant>,
    val referees: List<User>,
    val address: String,
    val coordinates: Coordinates,
)
