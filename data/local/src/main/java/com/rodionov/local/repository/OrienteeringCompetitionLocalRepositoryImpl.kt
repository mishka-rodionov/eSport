package com.rodionov.local.repository

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.orienteering.OrienteeringParticipantDao
import com.rodionov.local.dao.orienteering.OrienteeringResultDao
import com.rodionov.local.mappers.toDomain
import com.rodionov.local.mappers.toEntity

class OrienteeringCompetitionLocalRepositoryImpl(
    private val orienteeringCompetitionDao: OrienteeringCompetitionDao,
    private val participantGroupDao: ParticipantGroupDao,
    private val participantDao: OrienteeringParticipantDao,
    private val orienteeringResultDao: OrienteeringResultDao
) : OrienteeringCompetitionLocalRepository {

    override suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        return runCatching {

            // Сохраняем в базу
            val id = orienteeringCompetitionDao.insert(orienteeringCompetition.toEntity())

            // Извлекаем обратно (то, что реально лежит в БД)
            val savedEntity = orienteeringCompetitionDao.getCompetitionById(id)
                ?: throw IllegalStateException("Failed to fetch saved competition with id = $id")

            // Возвращаем доменную модель
            savedEntity.toDomain()
        }
    }

    override suspend fun saveCompetitions(orienteeringCompetition: List<OrienteeringCompetition>): Result<List<OrienteeringCompetition>> {
        return runCatching {

            if (orienteeringCompetition.isEmpty()) return@runCatching emptyList()

            // 1. Преобразуем в сущности
            val entities = orienteeringCompetition.map { it.toEntity() }

            // 2. Сохраняем
            val ids = orienteeringCompetitionDao.insertAll(entities)

            // 3. Извлекаем обратно по ID
            val savedEntities = ids.map { id ->
                orienteeringCompetitionDao.getCompetitionById(id)
                    ?: throw IllegalStateException("Failed to fetch saved competition with id = $id")
            }

            // 4. В domain
            savedEntities.map { it.toDomain() }
        }
    }

    override suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails> {
        return runCatching {
            orienteeringCompetitionDao.getCompetitionWithDetails(competitionId).toDomain()
        }
    }

    override suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>> {
        return runCatching {

            orienteeringCompetitionDao
                .getCompetitionsByUserId(userId)
                .map { it.toDomain() }
        }
    }

    override suspend fun getCompetition(competitionId: Long): Result<OrienteeringCompetition?> {
        return runCatching {
            orienteeringCompetitionDao.getCompetitionById(competitionId)?.toDomain()
        }
    }

    override suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any> {
        return runCatching {
            participantGroupDao.insertAll(participantGroups.map(ParticipantGroup::toEntity))
        }
    }

    override suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant?> {
        return runCatching {
            val participantId = participantDao.insertParticipant(participant.toEntity())
            participantDao.getParticipantById(participantId)?.toDomain()
        }
    }

    override suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>> {
        return runCatching {
            participantDao.getAllParticipants(competitionId).map { it.toDomain() }
        }
    }

    override suspend fun updateParticipants(participants: List<OrienteeringParticipant>): Result<Any> {
        return runCatching { participantDao.updateAll(participants.map { it.toEntity() }) }
    }

    override suspend fun getParticipantByChipNumber(
        competitionId: Long,
        chipNumber: Int
    ): Result<OrienteeringParticipant> {
        return runCatching {
            participantDao.getParticipantByChipNumber(
                competitionId = competitionId,
                chipNumber = chipNumber
            ).toDomain()
        }
    }

    override suspend fun getParticipantGroup(groupId: Long): Result<ParticipantGroup> {
        return runCatching {
            participantGroupDao.getCertainParticipantGroup(groupId).toDomain()
        }
    }

    override suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult): Result<Any> {
        return runCatching {
            orienteeringResultDao.insertResult(orienteeringResult.toEntity())
        }
    }

    override suspend fun getResultForGroup(
        competitionId: Long,
        groupId: Long
    ): Result<List<OrienteeringResult>> {
        return runCatching {
            orienteeringResultDao.getResultsForCompetitionGroupDirect(competitionId = competitionId, groupId = groupId).map { it.toDomain() }
        }
    }

    override suspend fun updateResults(orienteeringResult: List<OrienteeringResult>): Result<Any> {
        return runCatching {
            orienteeringResultDao.updateResults(orienteeringResult.map { it.toEntity() })
        }
    }
}