package com.competra.local.dao

import androidx.room.*
import com.competra.local.entities.orienteering.ParticipantGroupEntity

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

    @Query("SELECT * FROM participant_groups WHERE groupId = :groupId")
    suspend fun getCertainParticipantGroup(groupId: Long) : ParticipantGroupEntity

    @Delete
    suspend fun delete(group: ParticipantGroupEntity)

    @Query("DELETE FROM participant_groups WHERE competitionId = :competitionId")
    suspend fun deleteGroupsForCompetition(competitionId: Long)

    @Update
    suspend fun updateParticipantGroup(participantGroup: ParticipantGroupEntity)

    @Query("DELETE FROM participant_groups")
    suspend fun clearAll()

    @Query("SELECT * FROM participant_groups WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsynced(): List<ParticipantGroupEntity>

    @Query("SELECT * FROM participant_groups WHERE isSynced = 0 AND isDeleted = 1")
    suspend fun getMarkedForDeletion(): List<ParticipantGroupEntity>
}
