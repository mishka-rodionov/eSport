package com.competra.local.mappers

import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.local.entities.orienteering.GroupWithParticipantsAndResultsEntity
import com.competra.local.entities.orienteering.ParticipantWithResultEntity

fun GroupWithParticipantsAndResultsEntity.toDomain(): GroupWithParticipantsAndResults {
    return GroupWithParticipantsAndResults(
        group = group.toDomain(),
        participants = participants.map(ParticipantWithResultEntity::toDomain)
    )
}

fun ParticipantWithResultEntity.toDomain(): ParticipantWithResult {
    return ParticipantWithResult(
        participant = participant.toDomain(),
        result = result?.toDomain()
    )
}