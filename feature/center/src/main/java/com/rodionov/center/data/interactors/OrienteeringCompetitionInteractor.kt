package com.rodionov.center.data.interactors

import android.util.Log
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringCompetitionDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository

/**
 * Интерактор для работы с соревнованиями по спортивному ориентированию.
 * Обеспечивает синхронизацию данных между локальным и удалённым репозиториями,
 * реализует основную бизнес-логику приложения.
 *
 * @param localRepository Репозиторий для работы с локальной базой данных
 * @param remoteRepository Репозиторий для работы с удалённым сервером
 */
class OrienteeringCompetitionInteractor(
    private val localRepository: OrienteeringCompetitionLocalRepository,
    private val remoteRepository: OrienteeringCompetitionRemoteRepository
) {

    /**
     * Сохраняет соревнование и группы участников.
     *
     * Функция реализует следующую логику:
     * 1. Пытается сохранить соревнование на сервере
     * 2. При успехе - сохраняет локально и создаёт группы участников на сервере
     * 3. При неудаче - сохраняет всё локально
     *
     * @param orienteeringCompetition Данные соревнования для сохранения
     * @param participantGroups Список групп участников
     * @return Результат операции в виде [OrienteeringCreatorAction]
     */
    suspend fun saveCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ): OrienteeringCreatorAction {
        //сетевой запрос на сохранение соревнования на сервере
        remoteRepository.createCompetition(orienteeringCompetition)
            .onSuccess { competition ->
                //сохранение данных соревнования локально
                localRepository.saveCompetition(competition).fold({
                    //сохранения данных по группам участников на сервере
                    createParticipantsGroupsInfo(
                        competitionId = competition.localCompetitionId,
                        participantGroups = participantGroups
                    )
                    return OrienteeringCreatorAction.SuccessfulCompetitionCreate
                }, {
                    //сохранения данных по группам участников на сервере
                    createParticipantsGroupsInfo(
                        competitionId = competition.localCompetitionId,
                        participantGroups = participantGroups
                    )
                    return OrienteeringCreatorAction.FailedCompetitionCreate("Сохранено на сервере, ошибка локального сохранения")
                }
                )
            }.onFailure {
                //локальное сохранение информации о соревновании
                localRepository.saveCompetition(orienteeringCompetition)
                    .fold({
                        //локальное сохранение информации о группах участников
                        localSaveParticipantGroups(participantGroups.map {
                            it.copy(
                                competitionId = orienteeringCompetition.localCompetitionId
                            )
                        })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, сохранено локально")
                    }, {
                        //локальное сохранение информации о группах участников
                        localSaveParticipantGroups(participantGroups.map {
                            it.copy(
                                competitionId = orienteeringCompetition.localCompetitionId
                            )
                        })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, ошибка локального созранения")
                    })

            }
        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
    }

    suspend fun saveCompetitionNew(orienteeringCompetition: OrienteeringCompetition): Result<OrienteeringCompetition> {

        return localRepository.saveCompetition(orienteeringCompetition)
    }

    /**
     * Публикует соревнование на сервере.
     * Вызывается в конце мастера создания соревнования.
     *
     * При успехе обновляет локальную запись данными, пришедшими с сервера.
     * При неудаче — локальные данные остаются нетронутыми.
     *
     * @param competition Данные соревнования для публикации.
     * @return Result с данными опубликованного соревнования или ошибкой.
     */
    suspend fun publishCompetitionToServer(competition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        return remoteRepository.createCompetition(competition)
            .onSuccess { serverCompetition ->
                localRepository.updateCompetition(serverCompetition)
            }
    }

    suspend fun publishGroupsToServer(
        remoteCompetitionId: Long,
        groups: List<ParticipantGroup>
    ): Result<Unit> {
        return remoteRepository.publishGroupsForCompetition(remoteCompetitionId, groups)
            .mapCatching { updatedGroups ->
                // Сохраняем remoteId и isSynced для каждой группы локально
                updatedGroups.forEach { localRepository.updateParticipantGroup(it) }
            }
    }

    suspend fun updateCompetitionNew(orienteeringCompetition: OrienteeringCompetition): Result<OrienteeringCompetition> {
        return localRepository.updateCompetition(orienteeringCompetition).mapCatching { orienteeringCompetition }
    }

    suspend fun updateCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>? = null
    ): OrienteeringCreatorAction {
        val competitionId = orienteeringCompetition.localCompetitionId

        // 1. Пытаемся обновить на сервере
        remoteRepository.updateCompetition(orienteeringCompetition).onSuccess {
            participantGroups?.let {
                remoteRepository.updateCompetitionParticipantsGroups(competitionId, participantGroups)
            }
        }

        // 2. Всегда обновляем локально
        localUpdate(orienteeringCompetition, participantGroups)

        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
    }

    suspend fun localUpdate(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>?
    ) {
        localRepository.updateCompetition(orienteeringCompetition).onSuccess {
            participantGroups?.let {
                localRepository.updateParticipantsGroups(orienteeringCompetition.localCompetitionId, participantGroups)
            }
        }.onFailure {

        }
    }

    /**
     * Получает соревнование по его идентификатору из локального хранилища.
     *
     * @param competitionId Идентификатор соревнования
     * @return Данные соревнования или null, если соревнование не найдено
     */
    suspend fun getCompetition(competitionId: Long): OrienteeringCompetition? {
        return localRepository.getCompetition(competitionId).getOrNull()
    }

    suspend fun getCompetitionWithDetails(competitionId: Long): Result<OrienteeringCompetitionDetails> {
        return localRepository.getCompetitionWithDetails(competitionId)
    }

    /**
     * Получает список соревнований пользователя.
     *
     * Реализует стратегию:
     * 1. Сначала пытается получить данные из сети
     * 2. При успехе - обновляет локальное хранилище
     * 3. При неудаче - возвращает данные из локального хранилища
     *
     * @param userId Идентификатор пользователя
     * @return Result со списком соревнований или ошибкой
     */
    suspend fun getCompetitionsByUserId(userId: String): Result<List<OrienteeringCompetition>> {
        // 1. Сначала пробуем получить из сети
        val remoteResult = remoteRepository.getCompetitionsByUserid(userId)

        remoteResult.onSuccess { competitions ->
            // 2. Если сеть успешна — обновляем локальное хранилище
            localRepository.saveCompetitions(competitions)
            val compet = localRepository.getCompetitionsByUserid(userId).getOrNull()
            Log.d("LOG_TAG", "getCompetitionsByUserId: size = ${compet?.size}")
            return Result.success(compet ?: competitions)
        }

        // 3. Если сеть упала — достаём локальные данные
        val localResult = localRepository.getCompetitionsByUserid(userId)

        localResult.onSuccess { localCompetitions ->
            return Result.success(localCompetitions)
        }

        // 4. Если вообще всё сломалось — возвращаем ошибку сети
        return Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error"))
    }

    /**
     * Сохраняет участника соревнования локально, затем синхронизирует с сервером.
     * При успешной синхронизации обновляет флаг isSynced в локальной БД.
     *
     * @param participant Данные участника для сохранения
     * @return Сохранённый участник или null в случае ошибки
     */
    suspend fun saveParticipant(participant: OrienteeringParticipant): OrienteeringParticipant? {
        val saved = localRepository.saveParticipant(participant).getOrNull() ?: return null
        remoteRepository.saveParticipant(saved).onSuccess {
            localRepository.updateParticipants(listOf(saved.copy(isSynced = true)))
        }
        return saved
    }

    /**
     * Возвращает существующий результат участника по его ID, или null если записи нет.
     */
    suspend fun getResultByParticipantId(participantId: Long): OrienteeringResult? {
        return localRepository.getResultByParticipant(participantId).getOrNull()
    }

    /**
     * Применяет конфликтующий результат: перезаписывает локальную запись (сохраняя её ID)
     * и синхронизирует с сервером.
     *
     * @param existingId Локальный ID существующей записи в БД.
     * @param newResult  Новые данные результата.
     */
    suspend fun applyConflictResult(existingId: Long, newResult: OrienteeringResult) {
        val updated = newResult.copy(id = existingId)
        localRepository.updateResults(listOf(updated)).onSuccess {
            updateResultsAndRanks(updated)
            remoteRepository.saveResult(updated)
        }
    }

    /**
     * Обновляет список участников в локальном хранилище.
     *
     * @param participants Список участников для обновления
     */
    suspend fun updateParticipants(participants: List<OrienteeringParticipant>) {
        localRepository.updateParticipants(participants = participants)
    }

    /**
     * Удаляет соревнование и все связанные данные из локальной базы данных.
     *
     * @param competitionId Идентификатор соревнования.
     * @return Result операции удаления.
     */
    suspend fun deleteCompetition(competitionId: Long): Result<Unit> {
        return localRepository.deleteCompetition(competitionId)
    }

    /**
     * Удаляет участника из локальной базы данных.
     *
     * @param participantId Идентификатор участника
     * @return Result операции удаления
     */
    suspend fun deleteParticipant(participantId: Long): Result<Unit> {
        return localRepository.deleteParticipant(participantId)
    }

    /**
     * Обновляет участника только в локальной базе данных.
     *
     * @param participant Участник для обновления
     * @return Result операции локального сохранения
     */
    suspend fun updateParticipantLocally(participant: OrienteeringParticipant): Result<Any> {
        return localRepository.updateParticipants(listOf(participant))
    }

    /**
     * Синхронизирует участника с сервером.
     * При успехе обновляет флаг isSynced в локальной БД.
     *
     * @param participant Участник для синхронизации
     */
    suspend fun syncParticipantWithServer(participant: OrienteeringParticipant) {
        remoteRepository.saveParticipant(participant).onSuccess {
            localRepository.updateParticipants(listOf(participant.copy(isSynced = true)))
        }
    }

    /**
     * Создаёт информацию о группах участников для соревнования.
     *
     * Внутренняя функция, которая:
     * 1. Пытается сохранить группы на сервере
     * 2. При успехе - сохраняет их локально
     * 3. При неудаче - сохраняет только локально
     *
     * @param competitionId Идентификатор соревнования
     * @param participantGroups Список групп участников для сохранения
     */
    suspend fun createParticipantsGroupsInfo(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ) {
        remoteRepository.createParticipantsGroupsForCompetition(
            competitionId = competitionId,
            participantGroups = participantGroups
        ).onSuccess { participants ->
            localSaveParticipantGroups(participants)
        }.onFailure {
            localSaveParticipantGroups(participantGroups)
        }

    }

    suspend fun localSaveParticipantGroups(participantGroups: List<ParticipantGroup>) {
        localRepository.saveParticipantsGroups(participantGroups)
    }

    suspend fun updateParticipantGroup(participantGroup: ParticipantGroup): Result<Any> {
        return localRepository.updateParticipantGroup(participantGroup)
    }

    /**
     * Получает список участников соревнования из локального хранилища.
     *
     * @param competitionId Идентификатор соревнования
     * @return Result со списком участников или ошибкой
     */
    suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>> {
        return localRepository.getParticipants(competitionId = competitionId)
    }

    /**
     * Получает участника соревнования по номеру чипа.
     *
     * @param competitionId Идентификатор соревнования
     * @param chipNumber Номер чипа участника
     * @return Result с данными участника или ошибкой
     */
    suspend fun getParticipantByChipNumber(
        competitionId: Long,
        chipNumber: Int
    ): Result<OrienteeringParticipant> {
        return localRepository.getParticipantByChipNumber(
            competitionId = competitionId,
            chipNumber = chipNumber
        )
    }

    /**
     * Получает группу участников по её идентификатору.
     *
     * @param groupId Идентификатор группы
     * @return Result с данными группы или ошибкой
     */
    suspend fun getParticipantGroup(groupId: Long): Result<ParticipantGroup> {
        return localRepository.getParticipantGroup(groupId)
    }

    /**
     * Сохраняет результат участника локально, пересчитывает места,
     * затем синхронизирует результат с сервером.
     * При успешной синхронизации обновляет флаг isSynced в локальной БД.
     */
    suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult) {
        localRepository.saveParticipantResult(orienteeringResult).onSuccess { savedId ->
            val savedResult = orienteeringResult.copy(id = savedId as Long)
            updateResultsAndRanks(newResults = savedResult)
            remoteRepository.saveResult(savedResult).onSuccess {
                localRepository.updateResults(listOf(savedResult.copy(isSynced = true)))
            }
        }
    }

    /**
     * Пересчитывает места (rank) для списка результатов соревнования.
     *
     * Алгоритм работы:
     * 1. Фильтрует только финишировавших участников (status = FINISHED)
     * 2. Сортирует их по возрастанию общего времени (totalTime + penaltyTime)
     * 3. Присваивает места, начиная с 1
     * 4. Обрабатывает участников с одинаковым временем - присваивает одинаковые места
     * 5. Сохраняет исходные места для не финишировавших участников
     *
     * @param results Исходный список результатов
     * @return Новый список результатов с пересчитанными местами
     */
    fun recalculateRanks(results: List<OrienteeringResult>): List<OrienteeringResult> {
        // Разделяем финишировавших и остальных
        val (finished, others) = results.partition { it.status == ResultStatus.FINISHED }

        if (finished.isEmpty()) return results

        // Сортируем финишировавших по общему времени (totalTime + penaltyTime)
        val sortedFinished = finished.sortedBy { result ->
            (result.totalTime ?: Long.MAX_VALUE) + (result.penaltyTime)
        }

        // Пересчитываем места с учетом возможных совпадений времени
        var currentRank = 1
        var previousTime: Long? = null
        var skipCount = 0

        val rankedFinished = sortedFinished.mapIndexed { index, result ->
            val totalTimeWithPenalty = (result.totalTime ?: Long.MAX_VALUE) + result.penaltyTime

            when {
                // Если это первый участник или время отличается от предыдущего
                previousTime == null || totalTimeWithPenalty != previousTime -> {
                    currentRank = index + 1 - skipCount
                    previousTime = totalTimeWithPenalty
                    result.copy(rank = currentRank)
                }
                // Если время совпадает с предыдущим - присваиваем то же место
                else -> {
                    skipCount++
                    result.copy(rank = currentRank)
                }
            }
        }

        // Возвращаем все результаты (финишировавшие с новыми местами, остальные без изменений)
        return rankedFinished + others
    }

    /**
     * Альтернативная версия с использованием groupBy для более компактной обработки
     */
    fun recalculateRanksV2(results: List<OrienteeringResult>): List<OrienteeringResult> {
        val (finished, others) = results.partition { it.status == ResultStatus.FINISHED }

        if (finished.isEmpty()) return results

        // Группируем по общему времени и сортируем группы
        val resultsByTime = finished
            .groupBy { (it.totalTime ?: Long.MAX_VALUE) + it.penaltyTime }
            .toSortedMap()

        var rank = 1
        val rankedFinished = resultsByTime.flatMap { (_, groupResults) ->
            groupResults.map { result ->
                result.copy(rank = rank)
            }.also { rank += groupResults.size }
        }

        return rankedFinished + others
    }

    /**
     * Расширение для списка OrienteeringResult - обновляет ранги и возвращает отсортированный список
     */
    fun List<OrienteeringResult>.withRecalculatedRanks(): List<OrienteeringResult> {
        return recalculateRanksV2(this).sortedBy { it.rank ?: Int.MAX_VALUE }
    }

    /**
     * Пример использования в Interactor/Repository:
     */
    suspend fun updateResultsAndRanks(
        newResults: OrienteeringResult
    ): List<OrienteeringResult> {
        // Получаем текущие результаты
        val currentResults = localRepository.getResultForGroup(
            competitionId = newResults.competitionId,
            groupId = newResults.groupId
        ).getOrNull() ?: emptyList()

        // Объединяем с новыми
        val allResults = (currentResults + newResults).distinctBy { it.participantId }

        // Пересчитываем места
        val updatedResults = allResults.withRecalculatedRanks()

        // Сохраняем обновленные результаты
        localRepository.updateResults(updatedResults)

        return updatedResults
    }

    suspend fun getResultsByGroups(competitionId: Long): Result<List<GroupWithParticipantsAndResults>> {
        return localRepository.getResultByGroups(competitionId)
    }

    /**
     * Утверждает результаты соревнования, делая их недоступными для редактирования.
     *
     * @param competitionId Идентификатор соревнования.
     * @return Результат операции.
     */
    suspend fun approveResults(competitionId: Long): Result<Any> {
        return localRepository.updateIsEditableForCompetition(competitionId, false)
    }

    /**
     * Обновляет результат участника и пересчитывает ранги в группе.
     *
     * @param orienteeringResult Результат для обновления.
     */
    suspend fun updateParticipantResult(orienteeringResult: OrienteeringResult) {
        localRepository.updateResults(listOf(orienteeringResult)).onSuccess {
            updateResultsAndRanks(orienteeringResult)
        }
    }

    /**
     * Сохраняет новую дистанцию для соревнования.
     * 
     * @param distance Модель дистанции.
     * @return Результат операции с ID сохраненной записи.
     */
    suspend fun saveDistance(distance: Distance): Result<Long> {
        return localRepository.saveDistance(distance)
    }

    /**
     * Обновляет существующую дистанцию.
     * 
     * @param distance Модель дистанции.
     * @return Результат операции.
     */
    suspend fun updateDistance(distance: Distance): Result<Any> {
        return localRepository.updateDistance(distance)
    }

    /**
     * Получает список дистанций соревнования.
     */
    suspend fun getDistances(competitionId: Long): Result<List<Distance>> {
        return localRepository.getDistances(competitionId)
    }

}
