package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult

interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<OrienteeringCompetition>
    suspend fun saveCompetitions(orienteeringCompetition: List<OrienteeringCompetition>): Result<List<OrienteeringCompetition>>
    suspend fun getCompetition(competitionId: Long): Result<OrienteeringCompetition?>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any>
    suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails>
    suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>>

    suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant?>

    suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>>
    suspend fun updateParticipants(participants: List<OrienteeringParticipant>) : Result<Any>

    suspend fun getParticipantByChipNumber(competitionId: Long, chipNumber: Int) : Result<OrienteeringParticipant>
    suspend fun getParticipantGroup(groupId: Long) : Result<ParticipantGroup>
    suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult): Result<Any>
    suspend fun getResultForGroup(competitionId: Long, groupId: Long): Result<List<OrienteeringResult>>
    suspend fun updateResults(orienteeringResult: List<OrienteeringResult>): Result<Any>
}