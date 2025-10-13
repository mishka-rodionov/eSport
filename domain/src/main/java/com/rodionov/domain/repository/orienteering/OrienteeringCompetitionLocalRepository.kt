package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails

interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any>
    suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails>

}