package com.rodionov.local.mappers

import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.local.entities.orienteering.GroupWithParticipantsAndResultsEntity
import com.rodionov.local.entities.orienteering.ParticipantWithResultEntity

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