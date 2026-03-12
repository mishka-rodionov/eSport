package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.remote.request.orienteering.ControlPointRequest
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest

fun OrienteeringCompetition.toRequest(): OrienteeringCompetitionRequest {
    return OrienteeringCompetitionRequest(
        competitionId,
        competition.toRequest(),
        direction.name,
        punchingSystem.name,
        startTimeMode.name,
        countdownTimer
    )
}

fun ParticipantGroup.toRequest(compId: Long? = null): ParticipantGroupRequest {
    return ParticipantGroupRequest(
        groupId, compId ?: competitionId, title, distance, countOfControls, maxTimeInMinute, controlPoints.map { it.toRequest() }
    )
}

fun ControlPoint.toRequest(): ControlPointRequest {
    return ControlPointRequest(
        number = number,
        role = role,
        score = score
    )
}