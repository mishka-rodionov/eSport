package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.remote.request.orienteering.ControlPointRequest
import com.rodionov.remote.request.orienteering.OrienteeringCompetitionRequest
import com.rodionov.remote.request.orienteering.OrienteeringParticipantRequest
import com.rodionov.remote.request.orienteering.OrienteeringResultRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest
import com.rodionov.remote.request.orienteering.SplitTimeRequest

fun OrienteeringCompetition.toRequest(): OrienteeringCompetitionRequest {
    return OrienteeringCompetitionRequest(
        localCompetitionId,
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

fun OrienteeringParticipant.toRequest(): OrienteeringParticipantRequest {
    return OrienteeringParticipantRequest(
        id = id,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        groupId = groupId,
        groupName = groupName,
        competitionId = competitionId,
        commandName = commandName,
        startNumber = startNumber,
        startTime = startTime,
        chipNumber = chipNumber,
        comment = comment,
        isChipGiven = isChipGiven
    )
}

fun OrienteeringResult.toRequest(): OrienteeringResultRequest {
    return OrienteeringResultRequest(
        id = id,
        competitionId = competitionId,
        groupId = groupId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        status = status.name,
        penaltyTime = penaltyTime,
        splits = splits?.map { SplitTimeRequest(it.controlPoint, it.timestamp) },
        isEditable = isEditable,
        isEdited = isEdited
    )
}