package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

data class OrienteeringCompetitionWithDetails(
    @Embedded
    val competition: OrienteeringCompetitionEntity,

    @Relation(
        entity = ParticipantGroupEntity::class, // Указываем промежуточную сущность
        parentColumn = "id", // Поле из OrienteeringCompetitionEntity
        entityColumn = "competitionId" // Поле из ParticipantGroupEntity
    )
    val groupsWithParticipants: List<ParticipantGroupWithParticipants>
)