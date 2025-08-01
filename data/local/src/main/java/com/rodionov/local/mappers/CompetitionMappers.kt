package com.rodionov.local.mappers

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity

fun OrienteeringCompetition.toEntity(): OrienteeringCompetitionEntity {
    return OrienteeringCompetitionEntity(
        id = this.id, // Используем тот же ID
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
        id = this.id, // Используем тот же ID
        competition = this.competition, // Встраиваемый объект копируется напрямую
        direction = this.direction     // Enum копируется напрямую
    )
}

// Если вам нужно создавать список доменных моделей:
fun List<OrienteeringCompetitionEntity>.toDomainList(): List<OrienteeringCompetition> {
    return this.map { it.toDomain() }
}