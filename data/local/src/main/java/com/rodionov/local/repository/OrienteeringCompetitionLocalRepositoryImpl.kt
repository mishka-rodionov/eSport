package com.rodionov.local.repository

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.local.dao.OrienteeringCompetitionDao
import com.rodionov.local.dao.ParticipantGroupDao
import com.rodionov.local.dao.orienteering.OrienteeringParticipantDao
import com.rodionov.local.dao.orienteering.OrienteeringResultDao
import com.rodionov.local.dao.orienteering.DistanceDao
import com.rodionov.local.mappers.toDomain
import com.rodionov.local.mappers.toEntity

/**
 * Реализация локального репозитория для работы с соревнованиями по спортивному ориентированию.
 * Обеспечивает взаимодействие с Room DAO для выполнения операций с базой данных.
 * Конвертирует доменные модели в Entity и обратно.
 *
 * @param orienteeringCompetitionDao DAO для работы с соревнованиями
 * @param participantGroupDao DAO для работы с группами участников
 * @param participantDao DAO для работы с участниками
 * @param orienteeringResultDao DAO для работы с результатами
 * @param distanceDao DAO для работы с дистанциями
 */
class OrienteeringCompetitionLocalRepositoryImpl(
    private val orienteeringCompetitionDao: OrienteeringCompetitionDao,
    private val participantGroupDao: ParticipantGroupDao,
    private val participantDao: OrienteeringParticipantDao,
    private val orienteeringResultDao: OrienteeringResultDao,
    private val distanceDao: DistanceDao
) : OrienteeringCompetitionLocalRepository {

    /**
     * Сохраняет одиночное соревнование в локальную базу данных.
     *
     * Алгоритм:
     * 1. Конвертирует доменную модель в Entity
     * 2. Выполняет вставку в БД и получает ID записи
     * 3. Извлекает сохранённую запись по ID
     * 4. Конвертирует обратно в доменную модель
     *
     * @param orienteeringCompetition Доменная модель соревнования для сохранения
     * @return Result с сохранённой доменной моделью или ошибкой
     */
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

    /**
     * Сохраняет список соревнований в локальную базу данных.
     *
     * Алгоритм:
     * 1. Конвертирует список доменных моделей в Entity
     * 2. Выполняет массовую вставку и получает список ID
     * 3. Извлекает все сохранённые записи по ID
     * 4. Конвертирует обратно в доменные модели
     *
     * @param orienteeringCompetition Список доменных моделей соревнований для сохранения
     * @return Result со списком сохранённых доменных моделей или ошибкой
     */
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

    override suspend fun updateCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any> {
        return runCatching {
            orienteeringCompetitionDao.update(orienteeringCompetition.toEntity())
        }
    }

    /**
     * Получает соревнование с полной детализацией (включая группы и участников).
     *
     * @param competitionId Идентификатор соревнования
     * @return Result с детальной информацией о соревновании или ошибкой
     */
    override suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails> {
        return runCatching {
            orienteeringCompetitionDao.getCompetitionWithDetails(competitionId).toDomain()
        }
    }

    /**
     * Получает список соревнований по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя (организатора)
     * @return Result со списком соревнований пользователя или ошибкой
     */
    override suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>> {
        return runCatching {

            orienteeringCompetitionDao
                .getCompetitionsByUserId(userId)
                .map { it.toDomain() }
        }
    }

    /**
     * Получает одиночное соревнование по его идентификатору.
     *
     * @param competitionId Идентификатор соревнования
     * @return Result с соревнованием (или null, если не найдено) или ошибкой
     */
    override suspend fun getCompetition(competitionId: Long): Result<OrienteeringCompetition?> {
        return runCatching {
            orienteeringCompetitionDao.getCompetitionById(competitionId)?.toDomain()
        }
    }

    /**
     * Сохраняет группы участников для соревнования.
     *
     * @param participantGroups Список доменных моделей групп участников
     * @return Result с результатом операции или ошибкой
     */
    override suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any> {
        return runCatching {
            participantGroupDao.insertAll(participantGroups.map(ParticipantGroup::toEntity))
        }
    }

    override suspend fun updateParticipantsGroups(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ): Result<Any> {
        return runCatching {
            val existingGroups = participantGroupDao.getGroupsForCompetition(competitionId)
            val existingIds = existingGroups.map { it.groupId }.toSet()
            val incomingIds = participantGroups.filter { it.groupId != 0L }.map { it.groupId }.toSet()

            // Удаляем только те группы, которые были убраны пользователем
            existingGroups
                .filter { it.groupId !in incomingIds }
                .forEach { participantGroupDao.delete(it) }

            // Обновляем существующие группы и вставляем новые
            participantGroups.forEach { group ->
                val entity = group.toEntity().copy(competitionId = competitionId)
                if (entity.groupId != 0L && entity.groupId in existingIds) {
                    participantGroupDao.updateParticipantGroup(entity)
                } else {
                    participantGroupDao.insert(entity)
                }
            }
        }
    }

    override suspend fun updateParticipantGroup(participantGroup: ParticipantGroup): Result<Any> {
        return runCatching {
            participantGroupDao.updateParticipantGroup(participantGroup.toEntity())
        }
    }

    /**
     * Сохраняет одиночного участника соревнования.
     *
     * Алгоритм:
     * 1. Конвертирует доменную модель в Entity
     * 2. Выполняет вставку и получает ID
     * 3. Извлекает сохранённого участника по ID
     * 4. Конвертирует обратно в доменную модель
     *
     * @param participant Доменная модель участника
     * @return Result с сохранённым участником (или null) или ошибкой
     */
    override suspend fun saveParticipant(participant: OrienteeringParticipant): Result<OrienteeringParticipant?> {
        return runCatching {
            val participantId = participantDao.insertParticipant(participant.toEntity())
            participantDao.getParticipantById(participantId)?.toDomain()
        }
    }

    /**
     * Получает список участников соревнования.
     *
     * @param competitionId Идентификатор соревнования
     * @return Result со списком участников или ошибкой
     */
    override suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>> {
        return runCatching {
            participantDao.getAllParticipants(competitionId).map { it.toDomain() }
        }
    }

    /**
     * Обновляет информацию об участниках.
     *
     * @param participants Список доменных моделей участников для обновления
     * @return Result с результатом операции или ошибкой
     */
    override suspend fun updateParticipants(participants: List<OrienteeringParticipant>): Result<Any> {
        return runCatching { participantDao.updateAll(participants.map { it.toEntity() }) }
    }

    override suspend fun deleteParticipant(participantId: Long): Result<Unit> {
        return runCatching { participantDao.deleteParticipantById(participantId) }
    }

    /**
     * Получает участника по номеру чипа в рамках конкретного соревнования.
     *
     * @param competitionId Идентификатор соревнования
     * @param chipNumber Номер чипа участника
     * @return Result с доменной моделью участника или ошибкой
     */
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

    /**
     * Получает группу участников по её идентификатору.
     *
     * @param groupId Идентификатор группы
     * @return Result с доменной моделью группы или ошибкой
     */
    override suspend fun getParticipantGroup(groupId: Long): Result<ParticipantGroup> {
        return runCatching {
            participantGroupDao.getCertainParticipantGroup(groupId).toDomain()
        }
    }

    /**
     * Сохраняет результат участника в соревновании.
     *
     * @param orienteeringResult Доменная модель результата
     * @return Result с результатом операции или ошибкой
     */
    override suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult): Result<Any> {
        return runCatching {
            orienteeringResultDao.insertResult(orienteeringResult.toEntity())
        }
    }

    override suspend fun getResultByParticipant(participantId: Long): Result<OrienteeringResult?> {
        return runCatching {
            orienteeringResultDao.getResultForParticipant(participantId)?.toDomain()
        }
    }

    /**
     * Получает результаты для конкретной группы в рамках соревнования.
     *
     * @param competitionId Идентификатор соревнования
     * @param groupId Идентификатор группы
     * @return Result со списком доменных моделей результатов или ошибкой
     */
    override suspend fun getResultForGroup(
        competitionId: Long,
        groupId: Long
    ): Result<List<OrienteeringResult>> {
        return runCatching {
            orienteeringResultDao.getResultsForCompetitionGroupDirect(competitionId = competitionId, groupId = groupId).map { it.toDomain() }
        }
    }

    /**
     * Обновляет список результатов участников.
     *
     * @param orienteeringResult Список доменных моделей результатов для обновления
     * @return Result с результатом операции или ошибкой
     */
    override suspend fun updateResults(orienteeringResult: List<OrienteeringResult>): Result<Any> {
        return runCatching {
            orienteeringResultDao.updateResults(orienteeringResult.map { it.toEntity() })
        }
    }

    override suspend fun getResultByGroups(competitionId: Long): Result<List<GroupWithParticipantsAndResults>> {
        return runCatching {
            orienteeringResultDao.getProtocolByCompetition(competitionId).map { it.toDomain() }
        }
    }

    override suspend fun updateIsEditableForCompetition(
        competitionId: Long,
        isEditable: Boolean
    ): Result<Any> {
        return runCatching {
            orienteeringResultDao.updateIsEditableForCompetition(competitionId, isEditable)
        }
    }

    /**
     * Сохраняет новую дистанцию в локальную базу данных.
     * 
     * @param distance Доменная модель дистанции.
     * @return Result с ID сохраненной записи или ошибкой.
     */
    override suspend fun saveDistance(distance: Distance): Result<Long> {
        return runCatching {
            distanceDao.insertDistance(distance.toEntity())
        }
    }

    /**
     * Возвращает список всех дистанций для указанного соревнования.
     * 
     * @param competitionId Идентификатор соревнования.
     * @return Result со списком доменных моделей дистанций или ошибкой.
     */
    override suspend fun getDistances(competitionId: Long): Result<List<Distance>> {
        return runCatching {
            distanceDao.getDistancesForCompetition(competitionId).map { it.toDomain() }
        }
    }

    /**
     * Обновляет данные существующей дистанции.
     * 
     * @param distance Доменная модель дистанции.
     */
    override suspend fun updateDistance(distance: Distance): Result<Any> {
        return runCatching {
            distanceDao.updateDistance(distance.toEntity())
        }
    }
}
