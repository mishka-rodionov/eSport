package com.rodionov.local.repository

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.mappers.toEntity

class OrienteeringCompetitionLocalRepositoryImpl(
    private val orienteeringCompetitionDao: OrienteeringCompetitionDao,
    private val participantGroupDao: ParticipantGroupDao
): OrienteeringCompetitionLocalRepository {

    override suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any> {
        return runCatching {
            orienteeringCompetitionDao.insert(competition = orienteeringCompetition.toEntity())
        }
    }

    override suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any> {
        return runCatching {
            participantGroupDao.insertAll(participantGroups.map(ParticipantGroup::toEntity))
        }
    }
}