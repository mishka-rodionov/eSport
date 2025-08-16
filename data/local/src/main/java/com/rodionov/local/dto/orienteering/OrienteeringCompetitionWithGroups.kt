package com.rodionov.local.dto.orienteering

import androidx.room.Embedded
import androidx.room.Relation
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity

/**
 * Модель для получения одним dao запросом данных о соревновании и его группах
 * */
data class OrienteeringCompetitionWithGroups(
    @Embedded
    val competition: OrienteeringCompetitionEntity,

    @Relation(
        parentColumn = "id",               // поле в OrienteeringCompetitionEntity
        entityColumn = "competitionId"     // поле в ParticipantGroupEntity
    )
    val groups: List<ParticipantGroupEntity>
)