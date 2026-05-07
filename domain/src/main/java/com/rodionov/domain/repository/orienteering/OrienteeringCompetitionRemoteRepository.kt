package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult

interface OrienteeringCompetitionRemoteRepository {

    suspend fun createCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition>

    suspend fun createParticipantsGroupsForCompetition(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>

    suspend fun publishGroupsForCompetition(remoteCompetitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>

    suspend fun getCompetitionById(competitionId: Long): Result<OrienteeringCompetition>

    suspend fun getCompetitionParticipantsGroups(competitionId: Long): Result<List<ParticipantGroup>>

    suspend fun updateCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition>

    suspend fun updateCompetitionParticipantsGroups(competitionId: Long, participantGroups: List<ParticipantGroup>): Result<List<ParticipantGroup>>

    suspend fun deleteCompetition(competitionId: Long): Result<Unit>

    suspend fun deleteCompetitionParticipantsGroups(competitionId: Long): Result<Unit>

    suspend fun getCompetitionsByUserid(): Result<List<OrienteeringCompetition>>

    /**
     * Соревнования, на которые текущий пользователь зарегистрирован как участник.
     */
    suspend fun getRegisteredCompetitions(): Result<List<OrienteeringCompetition>>

    suspend fun publishDistancesForCompetition(
        remoteCompetitionId: Long,
        localCompetitionId: Long,
        distances: List<Distance>
    ): Result<List<Distance>>

    suspend fun getDistancesForCompetition(
        remoteCompetitionId: Long,
        localCompetitionId: Long
    ): Result<List<Distance>>

    suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant>

    suspend fun saveParticipants(participants: List<OrienteeringParticipant>): Result<List<OrienteeringParticipant>>

    suspend fun getParticipantsForCompetition(remoteCompetitionId: Long): Result<List<OrienteeringParticipant>>

    suspend fun saveResult(result: OrienteeringResult): Result<OrienteeringResult>

    suspend fun getResultsByCompetition(competitionId: Long): Result<List<OrienteeringResult>>

    /**
     * Удаление сущностей на сервере (физическое). Используется SyncCenterWorker
     * после soft-delete на клиенте: запись помечена isDeleted=true,
     * Worker отправляет DELETE и при успехе физически удаляет локальную запись.
     */
    suspend fun deleteCompetitionRemotely(remoteCompetitionId: Long): Result<Unit>
    suspend fun deleteGroupRemotely(remoteGroupId: Long): Result<Unit>
    suspend fun deleteDistanceRemotely(remoteDistanceId: Long): Result<Unit>
    suspend fun deleteParticipantRemotely(participantId: String): Result<Unit>
    suspend fun deleteResultRemotely(resultId: String): Result<Unit>
}