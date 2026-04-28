package com.rodionov.center.data.results

import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.ui.BaseState

data class OrienteeringCompetitionResultsState(
    val groupsWithParticipantsAndResults: List<GroupWithParticipantsAndResults> = emptyList(),
    val isApproved: Boolean = false
): BaseState