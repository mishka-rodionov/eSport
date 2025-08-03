package com.rodionov.remote.repository

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.repository.OrienteeringCompetitionRemoteRepository
import com.rodionov.remote.datasource.OrienteeringCompetitionRemoteDataSource
import com.rodionov.remote.request.mappers.toRequest
import com.rodionov.remote.response.mappers.toDomain

data class OrienteeringCompetitionRemoteRemoteRepositoryImpl(
    private val orienteeringCompetitionRemoteDataSource: OrienteeringCompetitionRemoteDataSource
) :
    OrienteeringCompetitionRemoteRepository {

    override suspend fun createCompetition(competition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        return orienteeringCompetitionRemoteDataSource.createOrienteeringCompetition(competition.toRequest())
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun createParticipantsGroupsForCompetition(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ): Result<List<ParticipantGroup>> {
        return orienteeringCompetitionRemoteDataSource.createCompetitionParticipantGroup(
            participantGroups.map { it.toRequest(competitionId) }).mapCatching { it.result!!.map { gr -> gr.toDomain() } }
    }
}
