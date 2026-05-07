package com.rodionov.domain.repository.orienteering

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.Distance

/**
 * Интерфейс локального репозитория для работы с данными соревнований по ориентированию.
 *
 * Параметр [markUnsynced] на мутирующих методах:
 * - true (дефолт) — путь UI/Interactor: при сохранении проставляются isSynced=false,
 *   lastModified=now, syncError=null. Worker позже выгрузит запись на сервер.
 * - false — путь Worker и server→local pull: запись пишется как есть, isSynced/serverUpdatedAt
 *   устанавливаются вызывающим явно.
 */
interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition, markUnsynced: Boolean = true): Result<OrienteeringCompetition>
    suspend fun saveCompetitions(orienteeringCompetition: List<OrienteeringCompetition>, markUnsynced: Boolean = true): Result<List<OrienteeringCompetition>>
    suspend fun updateCompetition(orienteeringCompetition: OrienteeringCompetition, markUnsynced: Boolean = true): Result<Any>
    suspend fun getCompetition(competitionId: Long): Result<OrienteeringCompetition?>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>, markUnsynced: Boolean = true): Result<Any>
    suspend fun updateParticipantsGroups(competitionId: Long, participantGroups: List<ParticipantGroup>, markUnsynced: Boolean = true): Result<Any>
    suspend fun updateParticipantGroup(participantGroup: ParticipantGroup, markUnsynced: Boolean = true): Result<Any>
    suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails>
    suspend fun getCompetitionsByUserid(userId: String): Result<List<OrienteeringCompetition>>

    suspend fun deleteCompetition(competitionId: Long): Result<Unit>

    suspend fun saveParticipant(participant: OrienteeringParticipant, markUnsynced: Boolean = true): Result<OrienteeringParticipant?>

    suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>>
    suspend fun updateParticipants(participants: List<OrienteeringParticipant>, markUnsynced: Boolean = true) : Result<Any>
    suspend fun deleteParticipant(participantId: String): Result<Unit>

    suspend fun getParticipantByChipNumber(competitionId: Long, chipNumber: Int) : Result<OrienteeringParticipant>
    suspend fun getParticipantGroup(groupId: Long) : Result<ParticipantGroup>
    suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult, markUnsynced: Boolean = true): Result<Any>
    suspend fun getResultByParticipant(participantId: String): Result<OrienteeringResult?>
    suspend fun getResultForGroup(competitionId: Long, groupId: Long): Result<List<OrienteeringResult>>
    suspend fun updateResults(orienteeringResult: List<OrienteeringResult>, markUnsynced: Boolean = true): Result<Any>

    suspend fun getResultByGroups(competitionId: Long): Result<List<GroupWithParticipantsAndResults>>

    /**
     * Обновляет флаг возможности редактирования для всех результатов соревнования.
     *
     * @param competitionId Идентификатор соревнования.
     * @param isEditable Значение флага.
     */
    suspend fun updateIsEditableForCompetition(competitionId: Long, isEditable: Boolean): Result<Any>

    /**
     * Сохраняет новую дистанцию для соревнования.
     */
    suspend fun saveDistance(distance: Distance, markUnsynced: Boolean = true): Result<Long>

    /**
     * Получает дистанцию по её локальному идентификатору.
     */
    suspend fun getDistanceById(distanceId: Long): Result<Distance?>

    /**
     * Получает список дистанций соревнования.
     */
    suspend fun getDistances(competitionId: Long): Result<List<Distance>>

    /**
     * Обновляет данные существующей дистанции.
     */
    suspend fun updateDistance(distance: Distance, markUnsynced: Boolean = true): Result<Any>

    // ====== Запросы для SyncCenterWorker ======

    /** Соревнования, ожидающие выгрузки на сервер. */
    suspend fun getUnsyncedCompetitions(): List<OrienteeringCompetition>

    /** Соревнования, помеченные на удаление и ещё не синхронизированные. */
    suspend fun getCompetitionsMarkedForDeletion(): List<OrienteeringCompetition>

    /** Группы участников, ожидающие выгрузки. */
    suspend fun getUnsyncedGroups(): List<ParticipantGroup>

    /** Группы, помеченные на удаление. */
    suspend fun getGroupsMarkedForDeletion(): List<ParticipantGroup>

    /** Дистанции, ожидающие выгрузки. */
    suspend fun getUnsyncedDistances(): List<Distance>

    /** Дистанции, помеченные на удаление. */
    suspend fun getDistancesMarkedForDeletion(): List<Distance>

    /** Участники, ожидающие выгрузки. */
    suspend fun getUnsyncedParticipants(): List<OrienteeringParticipant>

    /** Участники, помеченные на удаление. */
    suspend fun getParticipantsMarkedForDeletion(): List<OrienteeringParticipant>

    /** Результаты, ожидающие выгрузки. */
    suspend fun getUnsyncedResults(): List<OrienteeringResult>

    /** Результаты, помеченные на удаление. */
    suspend fun getResultsMarkedForDeletion(): List<OrienteeringResult>
}
