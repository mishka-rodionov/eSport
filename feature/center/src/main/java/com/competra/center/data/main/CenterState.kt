package com.competra.center.data.main

import com.competra.domain.models.orienteering.OrienteeringCompetition
import com.competra.ui.BaseState

data class CenterState(
    val controlledEvents: List<OrienteeringCompetition> = emptyList(),
    val isAuthed: Boolean = false,
    val deletingCompetition: OrienteeringCompetition? = null,
) : BaseState
