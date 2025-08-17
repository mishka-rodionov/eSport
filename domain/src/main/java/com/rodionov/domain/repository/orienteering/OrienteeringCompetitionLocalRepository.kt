package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup

interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any>

}