package com.competra.local.dto.orienteering

import androidx.room.Embedded
import androidx.room.Relation
import com.competra.local.entities.orienteering.OrienteeringCompetitionEntity
import com.competra.local.entities.orienteering.ParticipantGroupEntity

/**
 * Модель для получения одним dao запросом данных о соревновании и его группах
 * */
data class OrienteeringCompetitionWithGroups(
    @Embedded
    val competition: OrienteeringCompetitionEntity,

    @Relation(
        parentColumn = "localCompetitionId",               // поле в OrienteeringCompetitionEntity
        entityColumn = "competitionId"     // поле в ParticipantGroupEntity
    )
    val groups: List<ParticipantGroupEntity>
)