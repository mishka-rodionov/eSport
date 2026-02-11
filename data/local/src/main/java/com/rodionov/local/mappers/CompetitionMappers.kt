package com.rodionov.local.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.OrienteeringParticipantEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity
import com.rodionov.local.entities.orienteering.OrienteeringResultEntity

fun OrienteeringCompetition.toEntity(): OrienteeringCompetitionEntity {
    return OrienteeringCompetitionEntity(
        id = this.competitionId, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction,     // Enum копируется напрямую (Room обработает через TypeConverter),
        punchingSystem = punchingSystem
    )
}

// Если вам нужно создавать список сущностей:
fun List<OrienteeringCompetition>.toEntityList(): List<OrienteeringCompetitionEntity> {
    return this.map { it.toEntity() }
}


// --- Маппер из сущности Room в доменную модель ---

fun OrienteeringCompetitionEntity.toDomain(): OrienteeringCompetition {
    return OrienteeringCompetition(
        competitionId = this.id, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction,     // Enum копируется напрямую
        punchingSystem = punchingSystem
    )
}

// Если вам нужно создавать список доменных моделей:
fun List<OrienteeringCompetitionEntity>.toCompetitionDomainList(): List<OrienteeringCompetition> {
    return this.map { it.toDomain() }
}

fun ParticipantGroup.toEntity(): ParticipantGroupEntity {
    return ParticipantGroupEntity(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        distance = this.distance,
        countOfControls = this.countOfControls,
        maxTimeInMinute = this.maxTimeInMinute,
        controlPoints = this.controlPoints
    )
}

fun ParticipantGroupEntity.toDomain(): ParticipantGroup {
    return ParticipantGroup(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        distance = this.distance,
        countOfControls = this.countOfControls,
        maxTimeInMinute = this.maxTimeInMinute,
        controlPoints = this.controlPoints

    )
}

fun OrienteeringParticipantEntity.toDomain(): OrienteeringParticipant {
    return OrienteeringParticipant(
        id = id,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        groupId = groupId,
        competitionId = competitionId,
        commandName = commandName,
        startNumber = startNumber,
        startTime = startTime,
        chipNumber = chipNumber,
        comment = comment
    )
}

fun OrienteeringParticipant.toEntity(): OrienteeringParticipantEntity {
    return OrienteeringParticipantEntity(
        id = id,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        groupId = groupId,
        competitionId = competitionId,
        commandName = commandName,
        startNumber = startNumber,
        startTime = startTime,
        chipNumber = chipNumber,
        comment = comment
    )
}

fun OrienteeringResult.toEntity(): OrienteeringResultEntity {
    return OrienteeringResultEntity(
        id = id,
        competitionId = competitionId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        rank = rank,
        status = status,
        penaltyTime = penaltyTime,
        splits = splits
    )
}

fun OrienteeringResultEntity.toDomain(): OrienteeringResult {
    return OrienteeringResult(
        id = id,
        competitionId = competitionId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        rank = rank,
        status = status,
        penaltyTime = penaltyTime,
        splits = splits
    )
}

fun List<OrienteeringResultEntity>.toDomainList(): List<OrienteeringResult> {
    return this.map { it.toDomain() }
}
