package com.competra.diary.data.interactors

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.domain.repository.diary.WorkoutLocalRepository

/**
 * Бизнес-логика тренировочного дневника. Все мутации идут только в локальную БД
 * (isSynced=false) — фоновая выгрузка на сервер выполняется WorkoutSyncWorker'ом
 * при появлении сети, без ручного enqueue из UI-слоя (см. `:core:sync`).
 */
class WorkoutInteractor(
    private val localRepository: WorkoutLocalRepository
) {

    suspend fun saveWorkout(workout: WorkoutWithDetails): Result<WorkoutWithDetails> =
        localRepository.saveWorkout(workout)

    suspend fun updateWorkout(workout: WorkoutWithDetails): Result<Any> =
        localRepository.updateWorkout(workout)

    suspend fun getWorkouts(): Result<List<WorkoutWithDetails>> = localRepository.getWorkouts()

    suspend fun getWorkoutById(id: Long): Result<WorkoutWithDetails?> = localRepository.getWorkoutById(id)

    suspend fun deleteWorkout(id: Long): Result<Unit> = localRepository.markWorkoutDeleted(id)
}
