package com.rodionov.local.dao

import androidx.room.*
import com.rodionov.local.entities.orienteering.ParticipantGroupEntity

@Dao
interface ParticipantGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: ParticipantGroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<ParticipantGroupEntity>)

    @Query("SELECT * FROM participant_groups WHERE competitionId = :competitionId")
    suspend fun getGroupsForCompetition(competitionId: Long): List<ParticipantGroupEntity>

    @Query("SELECT * FROM participant_groups")
    suspend fun getAll(): List<ParticipantGroupEntity>

    @Delete
    suspend fun delete(group: ParticipantGroupEntity)

    @Query("DELETE FROM participant_groups")
    suspend fun clearAll()
}
