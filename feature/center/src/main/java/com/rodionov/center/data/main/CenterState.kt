package com.rodionov.center.data.main

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import java.time.LocalDate

data class CenterState(
    val controlledEvents: List<Competition> = emptyList(),
    val isAuthed: Boolean = false,
//    val controlledEvents: List<OrienteeringCompetition> = mockEvents()
)
