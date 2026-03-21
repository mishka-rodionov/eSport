package com.rodionov.remote.request.mappers

import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.remote.request.orienteering.ParticipantGroupRequest

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
