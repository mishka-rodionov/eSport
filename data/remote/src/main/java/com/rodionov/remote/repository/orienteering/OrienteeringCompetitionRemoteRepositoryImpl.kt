package com.rodionov.remote.repository.orienteering

import android.util.Log
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.remote.datasource.orienteering.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.request.mappers.toRequest
import com.rodionov.remote.request.orienteering.ParticipantGroupPublishRequest
import com.rodionov.remote.response.mappers.toDomain

/**
 * Реализация удаленного репозитория для соревнований по спортивному ориентированию.
 *
 * @param orienteeringCompetitionRemoteDataSource Источник данных для соревнований по спортивному ориентированию.
 */
data class OrienteeringCompetitionRemoteRepositoryImpl(
    private val orienteeringCompetitionRemoteDataSource: OrienteeringCompetitionRemoteDataSource
) : OrienteeringCompetitionRemoteRepository {

    override suspend fun createCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        return orienteeringCompetitionRemoteDataSource.createOrienteeringCompetition(competition.toRequest())
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun createParticipantsGroupsForCompetition(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ): Result<List<ParticipantGroup>> {
        return orienteeringCompetitionRemoteDataSource.createCompetitionParticipantGroup(
            participantGroups.map { it.toRequest() })
            .mapCatching { it.result!!.map { gr -> gr.toDomain() } }
    }

    override suspend fun publishGroupsForCompetition(
        remoteCompetitionId: Long,
        participantGroups: List<ParticipantGroup>
    ): Result<List<ParticipantGroup>> {
        return orienteeringCompetitionRemoteDataSource.publishParticipantGroups(
            participantGroups.map { group ->
                ParticipantGroupPublishRequest(
                    groupId = group.remoteId,  // null для новых, Long для существующих
                    competitionId = remoteCompetitionId,
                    title = group.title,
                    gender = group.gender?.name,
                    minAge = group.minAge,
                    maxAge = group.maxAge,
                    distanceId = group.distanceId,
                    maxParticipants = group.maxParticipants
                )
            }
        ).mapCatching { response ->
            // Сопоставляем локальные группы с ответом сервера по порядку
            participantGroups.zip(response.result!!) { localGroup, serverGroup ->
                localGroup.copy(
                    remoteId = serverGroup.groupId,
                    isSynced = true,
                    lastModified = System.currentTimeMillis()
                )
            }
        }
    }

    override suspend fun publishDistancesForCompetition(
        remoteCompetitionId: Long,
        localCompetitionId: Long,
        distances: List<Distance>
    ): Result<List<Distance>> {
        val requests = distances.map { it.toRequest(remoteCompetitionId) }
        return orienteeringCompetitionRemoteDataSource.saveDistances(requests)
            .mapCatching { response ->
                // Сопоставляем локальные дистанции с серверными по порядку (zip),
                // сохраняя локальный id и проставляя remoteId из ответа
                distances.zip(response.result!!) { localDist, serverDist ->
                    localDist.copy(remoteId = serverDist.id, isSynced = true)
                }
            }
    }

    override suspend fun getCompetitionById(competitionId: Long): Result<OrienteeringCompetition> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionParticipantsGroups(competitionId: Long): Result<List<ParticipantGroup>> {
        return orienteeringCompetitionRemoteDataSource.getParticipantGroups(competitionId)
            .mapCatching { it.result!!.map { gr -> gr.toDomain() } }
    }

    override suspend fun getDistancesForCompetition(
        remoteCompetitionId: Long,
        localCompetitionId: Long
    ): Result<List<Distance>> {
        return orienteeringCompetitionRemoteDataSource.getDistances(remoteCompetitionId)
            .mapCatching { it.result!!.map { dist -> dist.toDomain(localCompetitionId) } }
    }

    override suspend fun updateCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCompetitionParticipantsGroups(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ): Result<List<ParticipantGroup>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCompetition(competitionId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCompetitionParticipantsGroups(competitionId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionsByUserid(): Result<List<OrienteeringCompetition>> {
        return orienteeringCompetitionRemoteDataSource.getCompetitionsByUserid()
            .mapCatching { it.result!!.map { comp -> comp.toDomain() } }
    }

    override suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant> {
        return orienteeringCompetitionRemoteDataSource.saveParticipant(participant.toRequest())
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun saveParticipants(participants: List<OrienteeringParticipant>): Result<List<OrienteeringParticipant>> {
        return orienteeringCompetitionRemoteDataSource
            .saveParticipants(participants.map { it.toRequest() })
            .mapCatching { it.result!!.map { r -> r.toDomain() } }
    }

    override suspend fun getParticipantsForCompetition(remoteCompetitionId: Long): Result<List<OrienteeringParticipant>> {
        return orienteeringCompetitionRemoteDataSource.getParticipantsByCompetition(remoteCompetitionId)
            .mapCatching {
                it.result!!
                    .map { p -> p.toDomain() }
            }
            .onFailure {
                Log.d("LOG_TAG", "getParticipantsForCompetition: ${it.message}")
            }
    }

    override suspend fun saveResult(result: OrienteeringResult): Result<OrienteeringResult> {
        return orienteeringCompetitionRemoteDataSource.saveResult(result.toRequest())
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun getResultsByCompetition(competitionId: Long): Result<List<OrienteeringResult>> {
        return orienteeringCompetitionRemoteDataSource.getResultsByCompetition(competitionId)
            .mapCatching { it.result!!.map { r -> r.toDomain() } }
    }
}
