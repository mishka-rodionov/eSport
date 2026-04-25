package com.rodionov.center.data.splits

import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.ui.BaseState

data class ParticipantSplitsState(
    val participant: OrienteeringParticipant? = null,
    val result: OrienteeringResult? = null
) : BaseState
