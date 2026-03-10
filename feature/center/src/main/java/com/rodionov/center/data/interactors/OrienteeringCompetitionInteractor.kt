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
                        competitionId = competition.competitionId,
                        participantGroups = participantGroups
                    )
                    return OrienteeringCreatorAction.SuccessfulCompetitionCreate
                }, {
                    //сохранения данных по группам участников на сервере
                    createParticipantsGroupsInfo(
                        competitionId = competition.competitionId,
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
                        localRepository.saveParticipantsGroups(
                            participantGroups.map {
                                it.copy(
                                    competitionId = orienteeringCompetition.competitionId
                                )
                            })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, сохранено локально")
                    }, {
                        //локальное сохранение информации о группах участников
                        localRepository.saveParticipantsGroups(
                            participantGroups.map {
                                it.copy(
                                    competitionId = orienteeringCompetition.competitionId
                                )
                            })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, ошибка локального созранения")
                    })

            }
        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
    }

    suspend fun updateCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ): OrienteeringCreatorAction {
        val competitionId = orienteeringCompetition.competitionId

        // 1. Пытаемся обновить на сервере
//        remoteRepository.updateCompetition(orienteeringCompetition).onSuccess {
//            remoteRepository.updateCompetitionParticipantsGroups(competitionId, participantGroups)
//        }

        // 2. Всегда обновляем локально
        localRepository.updateCompetition(orienteeringCompetition).onSuccess {
            localRepository.updateParticipantsGroups(competitionId, participantGroups)
            return OrienteeringCreatorAction.SuccessfulCompetitionCreate
        }.onFailure {
            return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка локального обновления")
        }

        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
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
     * Сохраняет участника соревнования в локальное хранилище.
     *
     * @param participant Данные участника для сохранения
     * @return Сохранённый участник или null в случае ошибки
     */
    suspend fun saveParticipant(participant: OrienteeringParticipant): OrienteeringParticipant? {
        return localRepository.saveParticipant(participant).getOrNull()
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
    private suspend fun createParticipantsGroupsInfo(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ) {
        remoteRepository.createParticipantsGroupsForCompetition(
            competitionId = competitionId,
            participantGroups = participantGroups
        ).onSuccess { participants ->
            localRepository.saveParticipantsGroups(participants)
        }.onFailure {
            localRepository.saveParticipantsGroups(participantGroups)
        }

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

    suspend fun saveParticipantResult(orienteeringResult: OrienteeringResult) {
        localRepository.saveParticipantResult(orienteeringResult).onSuccess {
            updateResultsAndRanks(
                newResults = orienteeringResult
            )
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

}