package com.rodionov.center.data

import java.time.LocalDate

data class OrienteeringCreatorState(
    val title: String = "",
    val date: LocalDate = LocalDate.of(1970, 1, 1),
    val address: String = ""
)