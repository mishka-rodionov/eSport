package com.rodionov.center.data.read_card

import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.SplitTime
import com.rodionov.ui.BaseState

data class OrientReadCardState(
    val participant: OrienteeringParticipant? = null,
    val participantResult: OrienteeringResult? = null,
    val rawSplits: List<SplitTime>? = null,
    val isCompetitionFinished: Boolean = false
) : BaseState
