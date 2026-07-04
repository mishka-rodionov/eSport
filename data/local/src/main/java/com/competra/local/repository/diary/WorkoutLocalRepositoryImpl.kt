package com.competra.local.repository.diary

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.domain.repository.diary.WorkoutLocalRepository
import com.competra.local.dao.diary.WorkoutDao
import com.competra.local.mappers.toDomain
import com.competra.local.mappers.toEntity

/**
 * Реализация локального репозитория тренировочного дневника.
 *
 * Мутирующие методы принимают `markUnsynced: Boolean`. Если true (дефолт) — это вызов из
 * UI/Interactor, и перед записью в БД проставляются isSynced=false, lastModified=now,
 * syncError=null. Если false — это запись из WorkoutSyncWorker или server→local pull.
 */
class WorkoutLocalRepositoryImpl(
    private val workoutDao: WorkoutDao
) : WorkoutLocalRepository {

    private fun WorkoutWithDetails.applyUnsynced(): WorkoutWithDetails =
        copy(workout = workout.copy(isSynced = false, lastModified = System.currentTimeMillis(), syncError = null))

    private suspend fun WorkoutWithDetails.persist(): Long {
        val workoutId = workoutDao.insertWorkout(workout.toEntity())

        workoutDao.deleteRunDetails(workoutId)
        workoutDao.deleteBikeDetails(workoutId)
        workoutDao.deleteSkiDetails(workoutId)

        runDetails?.copy(workoutId = workoutId)?.toEntity()?.let { workoutDao.insertRunDetails(it) }
        bikeDetails?.copy(workoutId = workoutId)?.toEntity()?.let { workoutDao.insertBikeDetails(it) }
        skiDetails?.copy(workoutId = workoutId)?.toEntity()?.let { workoutDao.insertSkiDetails(it) }

        return workoutId
    }

    override suspend fun saveWorkout(workout: WorkoutWithDetails, markUnsynced: Boolean): Result<WorkoutWithDetails> =
        runCatching {
            val toSave = if (markUnsynced) workout.applyUnsynced() else workout
            val workoutId = toSave.persist()
            workoutDao.getById(workoutId)?.toDomain()
                ?: throw IllegalStateException("Failed to fetch saved workout with id = $workoutId")
        }

    override suspend fun updateWorkout(workout: WorkoutWithDetails, markUnsynced: Boolean): Result<Any> =
        runCatching {
            val toSave = if (markUnsynced) workout.applyUnsynced() else workout
            toSave.persist()
        }

    override suspend fun getWorkouts(): Result<List<WorkoutWithDetails>> = runCatching {
        workoutDao.getAll().map { it.toDomain() }
    }

    override suspend fun getWorkoutById(id: Long): Result<WorkoutWithDetails?> = runCatching {
        workoutDao.getById(id)?.toDomain()
    }

    override suspend fun markWorkoutDeleted(id: Long): Result<Unit> = runCatching {
        val existing = workoutDao.getById(id)?.toDomain() ?: return@runCatching
        val marked = existing.copy(
            workout = existing.workout.copy(
                isDeleted = true,
                isSynced = false,
                lastModified = System.currentTimeMillis()
            )
        )
        marked.persist()
        Unit
    }

    override suspend fun getUnsyncedWorkouts(): List<WorkoutWithDetails> =
        workoutDao.getUnsynced().map { it.toDomain() }

    override suspend fun getWorkoutsMarkedForDeletion(): List<WorkoutWithDetails> =
        workoutDao.getMarkedForDeletion().map { it.toDomain() }

    override suspend fun purgeWorkoutLocally(id: Long) {
        workoutDao.deleteWorkout(id)
    }
}
