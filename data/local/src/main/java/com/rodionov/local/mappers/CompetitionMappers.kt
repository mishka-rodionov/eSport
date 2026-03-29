package com.rodionov.local.mappers

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.OrienteeringParticipantEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity
import com.rodionov.local.entities.orienteering.OrienteeringResultEntity
import com.rodionov.local.entities.orienteering.DistanceEntity

/**
 * Преобразование доменной модели соревнования в сущность базы данных.
 */
fun OrienteeringCompetition.toEntity(): OrienteeringCompetitionEntity {
    return OrienteeringCompetitionEntity(
        localCompetitionId = this.localCompetitionId, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction,     // Enum копируется напрямую (Room обработает через TypeConverter)
        punchingSystem = this.punchingSystem,
        startTimeMode = this.startTimeMode,
        countdownTimer = this.countdownTimer,
        startTime = this.startTime
    )
}

/**
 * Преобразование списка доменных моделей соревнований в список сущностей базы данных.
 */
fun List<OrienteeringCompetition>.toEntityList(): List<OrienteeringCompetitionEntity> {
    return this.map { it.toEntity() }
}


// --- Маппер из сущности Room в доменную модель ---

/**
 * Преобразование сущности соревнования из базы данных в доменную модель.
 */
fun OrienteeringCompetitionEntity.toDomain(): OrienteeringCompetition {
    return OrienteeringCompetition(
        localCompetitionId = this.localCompetitionId, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction,     // Enum копируется напрямую
        punchingSystem = this.punchingSystem,
        startTimeMode = this.startTimeMode,
        countdownTimer = this.countdownTimer,
        startTime = this.startTime
    )
}

/**
 * Преобразование списка сущностей соревнований в список доменных моделей.
 */
fun List<OrienteeringCompetitionEntity>.toCompetitionDomainList(): List<OrienteeringCompetition> {
    return this.map { it.toDomain() }
}

/**
 * Преобразование доменной модели группы участников в сущность базы данных.
 */
fun ParticipantGroup.toEntity(): ParticipantGroupEntity {
    return ParticipantGroupEntity(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        gender = this.gender,
        minAge = this.minAge,
        maxAge = this.maxAge,
        distanceId = this.distanceId,
        maxParticipants = this.maxParticipants,
        isSynced = this.isSynced,
        lastModified = this.lastModified,
        isDeleted = this.isDeleted
    )
}

/**
 * Преобразование сущности группы участников из базы данных в доменную модель.
 */
fun ParticipantGroupEntity.toDomain(): ParticipantGroup {
    return ParticipantGroup(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        gender = this.gender,
        minAge = this.minAge,
        maxAge = this.maxAge,
        distanceId = this.distanceId,
        maxParticipants = this.maxParticipants,
        isSynced = this.isSynced,
        lastModified = this.lastModified,
        isDeleted = this.isDeleted
    )
}

/**
 * Преобразование сущности участника из базы данных в доменную модель.
 */
fun OrienteeringParticipantEntity.toDomain(): OrienteeringParticipant {
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
        isChipGiven = isChipGiven
    )
}

/**
 * Преобразование доменной модели участника в сущность базы данных.
 */
fun OrienteeringParticipant.toEntity(): OrienteeringParticipantEntity {
    return OrienteeringParticipantEntity(
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

/**
 * Преобразование доменной модели результата в сущность базы данных.
 */
fun OrienteeringResult.toEntity(): OrienteeringResultEntity {
    return OrienteeringResultEntity(
        id = id,
        competitionId = competitionId,
        groupId = groupId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        rank = rank,
        status = status,
        penaltyTime = penaltyTime,
        splits = splits,
        isEditable = isEditable,
        isEdited = isEdited
    )
}

/**
 * Преобразование сущности результата из базы данных в доменную модель.
 */
fun OrienteeringResultEntity.toDomain(): OrienteeringResult {
    return OrienteeringResult(
        id = id,
        competitionId = competitionId,
        groupId = groupId,
        participantId = participantId,
        startTime = startTime,
        finishTime = finishTime,
        totalTime = totalTime,
        rank = rank,
        status = status,
        penaltyTime = penaltyTime,
        splits = splits,
        isEditable = isEditable,
        isEdited = isEdited
    )
}

/**
 * Преобразование списка сущностей результатов в список доменных моделей.
 */
fun List<OrienteeringResultEntity>.toDomainList(): List<OrienteeringResult> {
    return this.map { it.toDomain() }
}

/**
 * Преобразование доменной модели дистанции в сущность базы данных.
 */
fun Distance.toEntity(): DistanceEntity {
    return DistanceEntity(
        id = this.id, // Используем ID из доменной модели для корректного обновления
        remoteId = this.remoteId,
        competitionId = this.competitionId,
        name = this.name,
        lengthMeters = this.lengthMeters,
        climbMeters = this.climbMeters,
        controlsCount = this.controlsCount,
        description = this.description,
        isSynced = this.isSynced,
        lastModified = this.lastModified,
        isDeleted = this.isDeleted,
        controlPoints = this.controlPoints
    )
}

/**
 * Преобразование сущности дистанции из базы данных в доменную модель.
 */
fun DistanceEntity.toDomain(): Distance {
    return Distance(
        id = this.id, // Передаем ID обратно в доменную модель
        remoteId = this.remoteId,
        competitionId = this.competitionId,
        name = this.name,
        lengthMeters = this.lengthMeters,
        climbMeters = this.climbMeters,
        controlsCount = this.controlsCount,
        description = this.description,
        isSynced = this.isSynced,
        lastModified = this.lastModified,
        isDeleted = this.isDeleted,
        controlPoints = this.controlPoints
    )
}
