package com.competra.domain.repository.diary

import com.competra.domain.models.diary.WorkoutWithDetails

/** Интерфейс удалённого репозитория тренировочного дневника. */
interface WorkoutRemoteRepository {

    /** Batch upsert локальных тренировок на сервер. */
    suspend fun saveWorkouts(workouts: List<WorkoutWithDetails>): Result<List<WorkoutWithDetails>>

    /** Server → local pull всех тренировок текущего пользователя. */
    suspend fun getWorkouts(): Result<List<WorkoutWithDetails>>

    suspend fun deleteWorkoutRemotely(remoteId: Long): Result<Unit>
}
