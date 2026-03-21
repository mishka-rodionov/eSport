package com.rodionov.remote.repository.orienteering

import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import com.rodionov.remote.datasource.orienteering.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.request.mappers.toRequest
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

    override suspend fun getCompetitionById(competitionId: Long): Result<OrienteeringCompetition> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionParticipantsGroups(competitionId: Long): Result<List<ParticipantGroup>> {
        TODO("Not yet implemented")
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

    override suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>> {
        return orienteeringCompetitionRemoteDataSource.getCompetitionsByUserid(userId)
            .mapCatching { it.result!!.map { comp -> comp.toDomain() } }
    }
}
