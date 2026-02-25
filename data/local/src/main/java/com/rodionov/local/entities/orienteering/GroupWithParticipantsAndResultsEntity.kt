package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

data class GroupWithParticipantsAndResultsEntity(

    @Embedded
    val group: ParticipantGroupEntity,

    @Relation(
        entity = OrienteeringParticipantEntity::class,
        parentColumn = "groupId",     // group.groupId
        entityColumn = "groupId"      // participant.groupId
    )
    val participants: List<ParticipantWithResultEntity>
)
