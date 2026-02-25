package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

data class ParticipantWithResultEntity(
    @Embedded
    val participant: OrienteeringParticipantEntity,

    @Relation(
        parentColumn = "id",              // participant.id
        entityColumn = "participantId"    // result.participantId
    )
    val result: OrienteeringResultEntity?
)
