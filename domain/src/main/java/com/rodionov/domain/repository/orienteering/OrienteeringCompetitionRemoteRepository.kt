package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup

interface OrienteeringCompetitionRemoteRepository {

    suspend fun createCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition>

    suspend fun createParticipantsGroupsForCompetition(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>

    suspend fun getCompetitionById(competitionId: Long): Result<OrienteeringCompetition>

    suspend fun getCompetitionParticipantsGroups(competitionId: Long): Result<List<ParticipantGroup>>

    suspend fun updateCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition>

    suspend fun updateCompetitionParticipantsGroups(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>

    suspend fun deleteCompetition(competitionId: Long): Result<Unit>

    suspend fun deleteCompetitionParticipantsGroups(competitionId: Long): Result<Unit>

    suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>>

}