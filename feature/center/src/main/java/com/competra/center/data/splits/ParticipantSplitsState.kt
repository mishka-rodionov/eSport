package com.competra.center.data.splits

import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.ui.BaseState

data class ParticipantSplitsState(
    val participant: OrienteeringParticipant? = null,
    val result: OrienteeringResult? = null
) : BaseState
