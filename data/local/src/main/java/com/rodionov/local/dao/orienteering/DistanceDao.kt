package com.rodionov.local.dao.orienteering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodionov.local.entities.orienteering.DistanceEntity

/**
 * DAO для работы с дистанциями соревнований в локальной БД.
 */
@Dao
interface DistanceDao {

    /**
     * Сохраняет новую дистанцию.
     * 
     * @param distance Сущность дистанции.
     * @return ID сохраненной записи.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistance(distance: DistanceEntity): Long

    /**
     * Обновляет существующую дистанцию.
     * 
     * @param distance Сущность дистанции.
     */
    @Update
    suspend fun updateDistance(distance: DistanceEntity)

    /**
     * Получает список дистанций для конкретного соревнования.
     * 
     * @param competitionId Идентификатор соревнования.
     * @return Список сущностей дистанций.
     */
    @Query("SELECT * FROM distances WHERE competitionId = :competitionId")
    suspend fun getDistancesForCompetition(competitionId: Long): List<DistanceEntity>

    /**
     * Удаляет дистанцию по её идентификатору.
     * 
     * @param id Идентификатор дистанции.
     */
    @Query("DELETE FROM distances WHERE id = :id")
    suspend fun deleteDistance(id: Long)

    @Query("DELETE FROM distances WHERE competitionId = :competitionId")
    suspend fun deleteDistancesByCompetitionId(competitionId: Long)
}
