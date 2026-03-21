package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.remote.response.orienteering.ControlPointResponse
import com.rodionov.remote.response.orienteering.OrienteeringCompetitionResponse
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
