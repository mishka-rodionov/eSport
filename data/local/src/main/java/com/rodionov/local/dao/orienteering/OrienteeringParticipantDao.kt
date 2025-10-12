package com.rodionov.local.dao.orienteering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodionov.local.entities.orienteering.OrienteeringParticipantEntity

@Dao
interface OrienteeringParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<OrienteeringParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: OrienteeringParticipantEntity)

    @Update
    suspend fun updateParticipant(participant: OrienteeringParticipantEntity)

    @Query("SELECT * FROM orienteering_participants WHERE id = :id")
    suspend fun getParticipantById(id: Long): OrienteeringParticipantEntity?

    @Query("SELECT * FROM orienteering_participants WHERE groupId = :groupId")
    suspend fun getParticipantsByGroupId(groupId: Long): List<OrienteeringParticipantEntity>

    @Query("SELECT * FROM orienteering_participants")
    suspend fun getAllParticipants(): List<OrienteeringParticipantEntity>

    @Query("DELETE FROM orienteering_participants WHERE id = :id")
    suspend fun deleteParticipantById(id: Long)
}
