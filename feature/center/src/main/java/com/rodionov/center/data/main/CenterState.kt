package com.rodionov.center.data.main

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.ui.BaseState

data class CenterState(
    val controlledEvents: List<OrienteeringCompetition> = emptyList(),
    val isAuthed: Boolean = false,
) : BaseState
