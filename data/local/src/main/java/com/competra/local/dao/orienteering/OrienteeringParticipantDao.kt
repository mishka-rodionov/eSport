package com.competra.local.dao.orienteering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.competra.local.entities.orienteering.OrienteeringParticipantEntity

@Dao
interface OrienteeringParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<OrienteeringParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: OrienteeringParticipantEntity): Long

    @Update
    suspend fun updateParticipant(participant: OrienteeringParticipantEntity)

    @Update
    suspend fun updateAll(participants: List<OrienteeringParticipantEntity>)

    @Query("SELECT * FROM orienteering_participants WHERE id = :id")
    suspend fun getParticipantById(id: String): OrienteeringParticipantEntity?

    @Query("SELECT * FROM orienteering_participants WHERE groupId = :groupId")
    suspend fun getParticipantsByGroupId(groupId: Long): List<OrienteeringParticipantEntity>

    @Query("SELECT * FROM orienteering_participants WHERE competitionId = :competitionId")
    suspend fun getAllParticipants(competitionId: Long): List<OrienteeringParticipantEntity>

    @Query("SELECT * FROM orienteering_participants WHERE competitionId = :competitionId AND chipNumber = :chipNumber")
    suspend fun getParticipantByChipNumber(competitionId: Long, chipNumber: Int): OrienteeringParticipantEntity

    @Query("DELETE FROM orienteering_participants WHERE id = :id")
    suspend fun deleteParticipantById(id: String)

    @Query("DELETE FROM orienteering_participants WHERE competitionId = :competitionId")
    suspend fun deleteParticipantsByCompetitionId(competitionId: Long)

    @Query("SELECT * FROM orienteering_participants WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsynced(): List<OrienteeringParticipantEntity>

    @Query("SELECT * FROM orienteering_participants WHERE isSynced = 0 AND isDeleted = 1")
    suspend fun getMarkedForDeletion(): List<OrienteeringParticipantEntity>
}
