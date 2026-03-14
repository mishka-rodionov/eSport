package com.rodionov.local.dao.orienteering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rodionov.local.entities.orienteering.GroupWithParticipantsAndResultsEntity
import com.rodionov.local.entities.orienteering.OrienteeringResultEntity
import kotlinx.coroutines.flow.Flow


/**
 * DAO (Data Access Object) для работы с результатами соревнований в локальной базе данных.
 * Предоставляет методы для выполнения операций CRUD над таблицей результатов.
 */
@Dao
interface OrienteeringResultDao {

    /**
     * Вставляет один результат в базу данных.
     * При конфликте (существующая запись с тем же ID) заменяет её.
     *
     * @param result Сущность результата для вставки
     * @return ID вставленной записи
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: OrienteeringResultEntity): Long

    /**
     * Вставляет список результатов в базу данных.
     * При конфликте (существующая запись с тем же ID) заменяет её.
     *
     * @param results Список сущностей результатов для вставки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<OrienteeringResultEntity>)

    /**
     * Обновляет существующий результат в базе данных.
     * Поиск записи производится по ID.
     *
     * @param result Сущность результата с обновлёнными данными
     */
    @Update
    suspend fun updateResult(result: OrienteeringResultEntity)

    @Query("SELECT * FROM orienteering_results WHERE competitionId = :competitionId")
    suspend fun getResultsForCompetitionDirect(competitionId: Long): List<OrienteeringResultEntity>


    @Query("SELECT * FROM orienteering_results WHERE competitionId = :competitionId AND groupId = :groupId")
    suspend fun getResultsForCompetitionGroupDirect(
        competitionId: Long,
        groupId: Long
    ): List<OrienteeringResultEntity>

    @Update
    suspend fun updateResults(results: List<OrienteeringResultEntity>)

    /**
     * Обновляет ранги (места) для нескольких участников в рамках одного соревнования.
     * Метод выполняет пакетное обновление записей результатов, устанавливая для каждого
     * участника его итоговое место.
     *
     * @param competitionId Идентификатор соревнования
     * @param participantRanks Карта соответствия: ID участника -> занятое место
     */
    @Transaction
    suspend fun updateRanks(competitionId: Long, participantRanks: Map<Long, Int>) {
        val results = getResultsForCompetitionDirect(competitionId)
        val resultsByParticipant = results.associateBy { it.participantId }

        val updatedResults = participantRanks
            .mapNotNull { (participantId, rank) ->
                resultsByParticipant[participantId]?.copy(rank = rank)
            }

        if (updatedResults.isNotEmpty()) {
            updateResults(updatedResults)
        }
    }

    /**
     * Получает результат по его идентификатору.
     *
     * @param id Идентификатор результата
     * @return Сущность результата или null, если запись не найдена
     */
    @Query("SELECT * FROM orienteering_results WHERE id = :id")
    suspend fun getResultById(id: Long): OrienteeringResultEntity?

    /**
     * Получает Flow со списком результатов для указанного соревнования.
     * Flow автоматически обновляется при изменениях в таблице.
     *
     * @param competitionId Идентификатор соревнования
     * @return Flow со списком результатов
     */
    @Query("SELECT * FROM orienteering_results WHERE competitionId = :competitionId")
    fun getResultsForCompetition(competitionId: Long): Flow<List<OrienteeringResultEntity>>

    /**
     * Получает результат конкретного участника в соревновании.
     *
     * @param participantId Идентификатор участника
     * @return Сущность результата или null, если результат не найден
     */
    @Query("SELECT * FROM orienteering_results WHERE participantId = :participantId")
    suspend fun getResultForParticipant(participantId: Long): OrienteeringResultEntity?

    /**
     * Удаляет результат по его идентификатору.
     *
     * @param id Идентификатор результата для удаления
     */
    @Query("DELETE FROM orienteering_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)

    /**
     * Удаляет все результаты указанного соревнования.
     *
     * @param competitionId Идентификатор соревнования
     */
    @Query("DELETE FROM orienteering_results WHERE competitionId = :competitionId")
    suspend fun deleteResultsByCompetitionId(competitionId: Long)

    @Transaction
    @Query("SELECT * FROM participant_groups")
    suspend fun getFullProtocol(): List<GroupWithParticipantsAndResultsEntity>

    @Transaction
    @Query(
        "SELECT * FROM participant_groups WHERE competitionId = :competitionId"
    )
    suspend fun getProtocolByCompetition(
        competitionId: Long
    ): List<GroupWithParticipantsAndResultsEntity>

    /**
     * Обновляет флаг возможности редактирования для всех результатов соревнования.
     *
     * @param competitionId Идентификатор соревнования
     * @param isEditable Новое значение флага
     */
    @Query("UPDATE orienteering_results SET isEditable = :isEditable WHERE competitionId = :competitionId")
    suspend fun updateIsEditableForCompetition(competitionId: Long, isEditable: Boolean)

}
