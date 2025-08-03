package com.rodionov.domain.repository

import com.rodionov.domain.models.  OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup

interface OrienteeringCompetitionRemoteRepository {

    suspend fun createCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition>

    suspend fun createParticipantsGroupsForCompetition(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>


}