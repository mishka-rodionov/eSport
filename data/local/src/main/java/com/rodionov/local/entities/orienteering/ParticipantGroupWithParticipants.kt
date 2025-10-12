package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

data class ParticipantGroupWithParticipants(
    @Embedded
    val group: ParticipantGroupEntity,
    @Relation(
        parentColumn = "groupId", // Первичный ключ в ParticipantGroupEntity
        entityColumn = "groupId"  // Внешний ключ в ParticipantEntity
    )
    val participants: List<OrienteeringParticipantEntity>
)
