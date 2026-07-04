package com.competra.local.dao.diary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.competra.local.entities.diary.BikeDetailsEntity
import com.competra.local.entities.diary.RunDetailsEntity
import com.competra.local.entities.diary.SkiDetailsEntity
import com.competra.local.entities.diary.WorkoutEntity
import com.competra.local.entities.diary.WorkoutWithDetailsEntity

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunDetails(details: RunDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBikeDetails(details: BikeDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkiDetails(details: SkiDetailsEntity)

    @Query("DELETE FROM run_details WHERE workoutId = :workoutId")
    suspend fun deleteRunDetails(workoutId: Long)

    @Query("DELETE FROM bike_details WHERE workoutId = :workoutId")
    suspend fun deleteBikeDetails(workoutId: Long)

    @Query("DELETE FROM ski_details WHERE workoutId = :workoutId")
    suspend fun deleteSkiDetails(workoutId: Long)

    @Transaction
    @Query("SELECT * FROM workouts WHERE isDeleted = 0 ORDER BY COALESCE(startedAt, scheduledDate) DESC")
    suspend fun getAll(): List<WorkoutWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): WorkoutWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM workouts WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsynced(): List<WorkoutWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM workouts WHERE isSynced = 0 AND isDeleted = 1")
    suspend fun getMarkedForDeletion(): List<WorkoutWithDetailsEntity>
}
