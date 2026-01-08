package com.rodionov.local.dao.orienteering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodionov.local.entities.orienteering.OrienteeringResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrienteeringResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: OrienteeringResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<OrienteeringResultEntity>)

    @Update
    suspend fun updateResult(result: OrienteeringResultEntity)

    @Query("SELECT * FROM orienteering_results WHERE id = :id")
    suspend fun getResultById(id: Long): OrienteeringResultEntity?

    @Query("SELECT * FROM orienteering_results WHERE competitionId = :competitionId")
    fun getResultsForCompetition(competitionId: Long): Flow<List<OrienteeringResultEntity>>

    @Query("SELECT * FROM orienteering_results WHERE participantId = :participantId")
    suspend fun getResultForParticipant(participantId: Long): OrienteeringResultEntity?

    @Query("DELETE FROM orienteering_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)

    @Query("DELETE FROM orienteering_results WHERE competitionId = :competitionId")
    suspend fun deleteResultsByCompetitionId(competitionId: Long)
}