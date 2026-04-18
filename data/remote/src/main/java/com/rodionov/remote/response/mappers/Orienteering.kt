package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.SplitTime
import com.rodionov.remote.response.orienteering.ControlPointResponse
import com.rodionov.remote.response.orienteering.DistanceResponse
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
 * groupId из ответа — UUID строка, сохраняется в remoteId.
 * Локальный groupId неизвестен на этом уровне, устанавливается при zip-сопоставлении.
 */
fun ParticipantGroupResponse.toDomain() : ParticipantGroup {
    return ParticipantGroup(
        groupId = 0L,
        competitionId = 0L,
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
        remoteId = groupId,
        isSynced = true,
        lastModified = System.currentTimeMillis()
    )
}

/**
 * Преобразует ответ сервера по дистанции в доменную модель.
 * localCompetitionId — локальный ID соревнования в Room, необходим для корректной привязки.
 */
fun DistanceResponse.toDomain(localCompetitionId: Long): Distance {
    return Distance(
        remoteId = id,
        competitionId = localCompetitionId,
        name = name,
        lengthMeters = lengthMeters,
        climbMeters = climbMeters,
        controlsCount = controlsCount,
        description = description,
        isSynced = true,
        controlPoints = emptyList()
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
        id = id.orEmpty(),
        userId = userId.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        groupId = groupId ?: 0L,
        groupName = groupName.orEmpty(),
        competitionId = competitionId ?: 0L,
        commandName = commandName.orEmpty(),
        startNumber = startNumber.orEmpty(),
        startTime = startTime ?: 0L,
        chipNumber = chipNumber.orEmpty(),
        comment = comment.orEmpty(),
        isChipGiven = isChipGiven ?: false,
        isSynced = true,
        remoteId = id    // server id сохраняем для последующего upsert
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
        participantId = participantId.toString(),
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
