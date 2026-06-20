package com.competra.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.competra.local.dto.orienteering.OrienteeringCompetitionWithGroups
import com.competra.local.entities.orienteering.OrienteeringCompetitionEntity
import com.competra.local.entities.orienteering.OrienteeringCompetitionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrienteeringCompetitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(competition: OrienteeringCompetitionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(competitions: List<OrienteeringCompetitionEntity>)

    @Update
    suspend fun update(competition: OrienteeringCompetitionEntity)

    @Query("SELECT * FROM orienteering_competitions")
    fun getAll(): Flow<List<OrienteeringCompetitionEntity>>

    @Query("SELECT * FROM orienteering_competitions WHERE competitionId = :id")
    suspend fun getCompetitionById(id: String): OrienteeringCompetitionEntity?

    @Query("SELECT * FROM orienteering_competitions WHERE competitionId IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<OrienteeringCompetitionEntity>

    @Delete
    suspend fun delete(competition: OrienteeringCompetitionEntity)

    @Query("DELETE FROM orienteering_competitions WHERE competitionId = :competitionId")
    suspend fun deleteById(competitionId: String)

    @Query("DELETE FROM orienteering_competitions")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM orienteering_competitions WHERE competitionId = :id")
    suspend fun getCompetitionWithGroups(id: String): OrienteeringCompetitionWithGroups

    @Transaction
    @Query("SELECT * FROM orienteering_competitions")
    suspend fun getAllCompetitionsWithGroups(): List<OrienteeringCompetitionWithGroups>

    @Transaction // Обязательно для вложенных связей!
    @Query("SELECT * FROM orienteering_competitions WHERE competitionId = :competitionId")
    suspend fun getCompetitionWithDetails(competitionId: String): OrienteeringCompetitionWithDetails

    @Transaction
    @Query("SELECT * FROM orienteering_competitions")
    suspend fun getAllCompetitionsWithDetails(): List<OrienteeringCompetitionWithDetails>

    @Query("SELECT * FROM orienteering_competitions WHERE mainOrganizerId = :userId")
    suspend fun getCompetitionsByUserId(userId: String): List<OrienteeringCompetitionEntity>

    @Query("SELECT * FROM orienteering_competitions WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsynced(): List<OrienteeringCompetitionEntity>

    @Query("SELECT * FROM orienteering_competitions WHERE isSynced = 0 AND isDeleted = 1")
    suspend fun getMarkedForDeletion(): List<OrienteeringCompetitionEntity>

}
