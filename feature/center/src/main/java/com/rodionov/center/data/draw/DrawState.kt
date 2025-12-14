package com.rodionov.center.data.draw

import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.ui.BaseState

data class DrawState(
    val participants: List<OrienteeringParticipant> = emptyList()
) : BaseState
