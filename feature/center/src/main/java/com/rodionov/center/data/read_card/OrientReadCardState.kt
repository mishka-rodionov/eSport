package com.rodionov.center.data.read_card

import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.ui.BaseState

data class OrientReadCardState(
    val participantResult: OrienteeringResult? = null
) : BaseState
