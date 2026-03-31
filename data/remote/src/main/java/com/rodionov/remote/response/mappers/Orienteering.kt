package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.SplitTime
import com.rodionov.remote.response.orienteering.ControlPointResponse
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
import com.rodionov.remote.response.orienteering.OrienteeringParticipantResponse
import com.rodionov.remote.response.orienteering.OrienteeringResultResponse
import com.rodionov.remote.response.orienteering.ParticipantGroupResponse

/**
 * Преобразует ответ сервера по соревнованию по ориентированию в доменную модель.
 */
fun OrienteeringCompetitionResponse.toDomain(): OrienteeringCompetition {
    return OrienteeringCompetition(
        competitionId, competition.toDomain(), OrienteeringDirection.valueOf(direction), punchingSystem, startTimeMode
    )
}

/**
 * Преобразует ответ сервера по группе участников в доменную модель.
 */
fun ParticipantGroupResponse.toDomain() : ParticipantGroup {
    return ParticipantGroup(
        groupId = groupId,
        competitionId = competitionId,
        title = title,
        gender = gender?.let { 
            try { 
                Gender.valueOf(it) 
            } catch (e: Exception) { 
                null 
            } 
        },
        minAge = minAge,
        maxAge = maxAge,
        distanceId = distanceId,
        maxParticipants = maxParticipants,
        isSynced = true,
        lastModified = System.currentTimeMillis()
    )
}

/**
 * Преобразует ответ сервера по контрольному пункту в доменную модель.
 */
fun ControlPointResponse.toDomain() : ControlPoint {
    return ControlPoint(
        number = number,
        role = role,
        score = score
    )
}

/**
 * Преобразует ответ сервера по участнику в доменную модель.
 */
fun OrienteeringParticipantResponse.toDomain(): OrienteeringParticipant {
    return OrienteeringParticipant(
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
        isChipGiven = isChipGiven,
        isSynced = true
    )
}

/**
 * Преобразует ответ сервера по результату в доменную модель.
 */
fun OrienteeringResultResponse.toDomain(): OrienteeringResult {
    return OrienteeringResult(
        id = id,
        competitionId = competitionId,
        groupId = groupId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        status = try { ResultStatus.valueOf(status) } catch (e: Exception) { ResultStatus.DNS },
        penaltyTime = penaltyTime,
        splits = splits?.map { SplitTime(it.controlPoint, it.timestamp) },
        isEditable = isEditable,
        isEdited = isEdited,
        isSynced = true
    )
}
