package com.rodionov.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rodionov.local.dto.orienteering.OrienteeringCompetitionWithGroups
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrienteeringCompetitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(competition: OrienteeringCompetitionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(competitions: List<OrienteeringCompetitionEntity>): List<Long>

    @Update
    suspend fun update(competition: OrienteeringCompetitionEntity)

    @Query("SELECT * FROM orienteering_competitions")
    fun getAll(): Flow<List<OrienteeringCompetitionEntity>>

    @Query("SELECT * FROM orienteering_competitions WHERE id = :id")
    suspend fun getCompetitionById(id: Long): OrienteeringCompetitionEntity?

    @Query("SELECT * FROM orienteering_competitions WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<OrienteeringCompetitionEntity>

    @Delete
    suspend fun delete(competition: OrienteeringCompetitionEntity)

    @Query("DELETE FROM orienteering_competitions")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM orienteering_competitions WHERE id = :id")
    suspend fun getCompetitionWithGroups(id: Long): OrienteeringCompetitionWithGroups

    @Transaction
    @Query("SELECT * FROM orienteering_competitions")
    suspend fun getAllCompetitionsWithGroups(): List<OrienteeringCompetitionWithGroups>

    @Transaction // Обязательно для вложенных связей!
    @Query("SELECT * FROM orienteering_competitions WHERE id = :competitionId")
    suspend fun getCompetitionWithDetails(competitionId: Long): OrienteeringCompetitionWithDetails

    @Transaction
    @Query("SELECT * FROM orienteering_competitions")
    suspend fun getAllCompetitionsWithDetails(): List<OrienteeringCompetitionWithDetails>

    @Query("SELECT * FROM orienteering_competitions WHERE mainOrganizer = :userId")
    suspend fun getCompetitionsByUserId(userId: String): List<OrienteeringCompetitionEntity>

}