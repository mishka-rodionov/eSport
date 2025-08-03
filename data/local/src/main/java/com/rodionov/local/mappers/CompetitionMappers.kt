package com.rodionov.local.mappers

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity

fun OrienteeringCompetition.toEntity(): OrienteeringCompetitionEntity {
    return OrienteeringCompetitionEntity(
        id = this.competitionId, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction     // Enum копируется напрямую (Room обработает через TypeConverter)
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
        direction = this.direction     // Enum копируется напрямую
    )
}

// Если вам нужно создавать список доменных моделей:
fun List<OrienteeringCompetitionEntity>.toDomainList(): List<OrienteeringCompetition> {
    return this.map { it.toDomain() }
}

fun ParticipantGroup.toEntity(): ParticipantGroupEntity {
    return ParticipantGroupEntity(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        distance = this.distance,
        countOfControls = this.countOfControls,
        maxTimeInMinute = this.maxTimeInMinute
    )
}

fun ParticipantGroupEntity.toModel(): ParticipantGroup {
    return ParticipantGroup(
        groupId = this.groupId,
        competitionId = this.competitionId,
        title = this.title,
        distance = this.distance,
        countOfControls = this.countOfControls,
        maxTimeInMinute = this.maxTimeInMinute
    )
}
