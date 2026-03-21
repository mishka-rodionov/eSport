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

/**
 * Преобразует доменную модель группы участников в модель запроса для сервера.
 */
fun ParticipantGroup.toRequest(): ParticipantGroupRequest {
    return ParticipantGroupRequest(
        groupId = groupId,
        competitionId = competitionId,
        title = title,
        gender = gender?.name,
        minAge = minAge,
        maxAge = maxAge,
        distanceId = distanceId,
        maxParticipants = maxParticipants
    )
}

fun ControlPoint.toRequest(): ControlPointRequest {
    return ControlPointRequest(
        number = number,
        role = role,
        score = score
    )
}