package com.competra.domain.repository.diary

import com.competra.domain.models.diary.WorkoutWithDetails

/**
 * Интерфейс локального репозитория тренировочного дневника.
 *
 * Параметр [markUnsynced] на мутирующих методах:
 * - true (дефолт) — путь UI/Interactor: при сохранении проставляются isSynced=false,
 *   lastModified=now, syncError=null. Worker позже выгрузит запись на сервер.
 * - false — путь Worker и server→local pull: запись пишется как есть.
 */
interface WorkoutLocalRepository {

    suspend fun saveWorkout(workout: WorkoutWithDetails, markUnsynced: Boolean = true): Result<WorkoutWithDetails>
    suspend fun updateWorkout(workout: WorkoutWithDetails, markUnsynced: Boolean = true): Result<Any>

    suspend fun getWorkouts(): Result<List<WorkoutWithDetails>>
    suspend fun getWorkoutById(id: Long): Result<WorkoutWithDetails?>

    /** Помечает тренировку на удаление (soft-delete) до выгрузки DELETE на сервер. */
    suspend fun markWorkoutDeleted(id: Long): Result<Unit>

    // ====== Запросы для WorkoutSyncWorker ======

    suspend fun getUnsyncedWorkouts(): List<WorkoutWithDetails>
    suspend fun getWorkoutsMarkedForDeletion(): List<WorkoutWithDetails>

    /** Физически удаляет тренировку из локальной БД (вызывается Worker'ом после успешного DELETE). */
    suspend fun purgeWorkoutLocally(id: Long)
}
