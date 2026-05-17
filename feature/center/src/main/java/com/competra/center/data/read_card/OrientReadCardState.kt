package com.competra.center.data.read_card

import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.SplitTime
import com.competra.ui.BaseState

data class OrientReadCardState(
    val participant: OrienteeringParticipant? = null,
    val participantResult: OrienteeringResult? = null,
    val rawSplits: List<SplitTime>? = null,
    val isCompetitionFinished: Boolean = false
) : BaseState
