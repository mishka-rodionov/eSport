package com.competra.remote.repository.diary

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.domain.repository.diary.WorkoutRemoteRepository
import com.competra.remote.datasource.diary.WorkoutRemoteDataSource
import com.competra.remote.request.mappers.toRequest
import com.competra.remote.response.mappers.toDomain

class WorkoutRemoteRepositoryImpl(
    private val workoutRemoteDataSource: WorkoutRemoteDataSource
) : WorkoutRemoteRepository {

    override suspend fun saveWorkouts(workouts: List<WorkoutWithDetails>): Result<List<WorkoutWithDetails>> {
        val requests = workouts.map { it.toRequest() }
        return workoutRemoteDataSource.saveWorkouts(requests).mapCatching { response ->
            // Сопоставляем локальные тренировки с серверными по порядку (zip), сохраняя
            // локальный id и проставляя remoteId/serverUpdatedAt из ответа.
            workouts.zip(response.result!!) { local, server ->
                val serverDomain = server.toDomain()
                serverDomain.copy(
                    workout = serverDomain.workout.copy(id = local.workout.id),
                    runDetails = serverDomain.runDetails?.copy(workoutId = local.workout.id),
                    bikeDetails = serverDomain.bikeDetails?.copy(workoutId = local.workout.id),
                    skiDetails = serverDomain.skiDetails?.copy(workoutId = local.workout.id)
                )
            }
        }
    }

    override suspend fun getWorkouts(): Result<List<WorkoutWithDetails>> =
        workoutRemoteDataSource.getWorkouts().mapCatching { it.result!!.map { w -> w.toDomain() } }

    override suspend fun deleteWorkoutRemotely(remoteId: Long): Result<Unit> =
        workoutRemoteDataSource.deleteWorkout(remoteId).mapCatching { }
}
