package com.rodionov.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rodionov.local.dto.orienteering.OrienteeringCompetitionWithGroups
import com.rodionov.local.entities.orienteering.OrienteeringCompetitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrienteeringCompetitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(competition: OrienteeringCompetitionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(competitions: List<OrienteeringCompetitionEntity>)

    @Query("SELECT * FROM orienteering_competitions")
    fun getAll(): Flow<List<OrienteeringCompetitionEntity>>

    @Query("SELECT * FROM orienteering_competitions WHERE id = :id")
    suspend fun getById(id: Long): OrienteeringCompetitionEntity?

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


    // --- транзакция для вставки сразу всего --- шаблон на будущее
//    @Transaction
//    suspend fun insertCompetitionWithGroups(
//        competition: OrienteeringCompetitionEntity,
//        groups: List<ParticipantGroupEntity>
//    ) {
//        // вставляем соревнование и получаем его id
//        val competitionId = insertCompetition(competition)
//
//        // проставляем competitionId во всех группах
//        val groupsWithId = groups.map { it.copy(competitionId = competitionId) }
//
//        // вставляем группы
//        insertGroups(groupsWithId)
//    }

}