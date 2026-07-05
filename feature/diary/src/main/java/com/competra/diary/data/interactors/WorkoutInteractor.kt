package com.competra.diary.data.interactors

import android.content.Context
import com.competra.core.sync.WorkoutSyncBootstrap
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.domain.repository.diary.WorkoutLocalRepository

/**
 * Бизнес-логика тренировочного дневника. Мутации идут в локальную БД (isSynced=false),
 * а следом ставится [WorkoutSyncBootstrap] — чтобы выгрузка на сервер не ждала следующего
 * появления сети или перезапуска приложения. Enqueue безопасен офлайн: WorkManager сам
 * отложит выполнение до появления NetworkType.CONNECTED (см. `:core:sync`).
 */
class WorkoutInteractor(
    private val localRepository: WorkoutLocalRepository,
    private val context: Context
) {

    suspend fun saveWorkout(workout: WorkoutWithDetails): Result<WorkoutWithDetails> =
        localRepository.saveWorkout(workout).onSuccess { enqueueSync() }

    suspend fun updateWorkout(workout: WorkoutWithDetails): Result<Any> =
        localRepository.updateWorkout(workout).onSuccess { enqueueSync() }

    suspend fun getWorkouts(): Result<List<WorkoutWithDetails>> = localRepository.getWorkouts()

    suspend fun getWorkoutById(id: Long): Result<WorkoutWithDetails?> = localRepository.getWorkoutById(id)

    suspend fun deleteWorkout(id: Long): Result<Unit> =
        localRepository.markWorkoutDeleted(id).onSuccess { enqueueSync() }

    private fun enqueueSync() {
        WorkoutSyncBootstrap.enqueue(context)
    }
}
