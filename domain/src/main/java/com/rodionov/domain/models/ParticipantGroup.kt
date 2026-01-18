package com.rodionov.domain.models

import com.rodionov.domain.models.orienteering.ControlPoint

data class ParticipantGroup(
    val groupId: Long,
    val competitionId: Long,
    val title: String,
    val distance: Double,
    val countOfControls: Int,
    val maxTimeInMinute: Int,
    val controlPoints: List<ControlPoint>
)