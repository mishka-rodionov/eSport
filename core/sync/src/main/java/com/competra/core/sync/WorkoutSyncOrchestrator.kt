package com.competra.core.sync

import android.util.Log
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.domain.repository.diary.WorkoutLocalRepository
import com.competra.domain.repository.diary.WorkoutRemoteRepository
import java.io.IOException

/**
 * Оркестрирует выгрузку локальных изменений тренировочного дневника на сервер.
 *
 * В отличие от [SyncOrchestrator] (`:feature:center`), здесь нет конфликт-резолвинга —
 * тренировки всегда однопользовательские, поэтому сервер просто перезаписывает запись
 * (last-write-wins), а не бросает 409.
 *
 * Pipeline: push unsynced → pull remote changes → soft-delete.
 *
 * @throws IOException пробрасывается transient-ошибка для перезапуска Worker'а через backoff.
 */
class WorkoutSyncOrchestrator(
    private val localRepository: WorkoutLocalRepository,
    private val remoteRepository: WorkoutRemoteRepository
) {

    suspend fun syncAll() {
        var transientFailure = false
        transientFailure = transientFailure or pushUnsynced()
        transientFailure = transientFailure or pullRemote()
        transientFailure = transientFailure or syncDeletes()

        if (transientFailure) {
            throw IOException("Transient workout sync error")
        }
    }

    private suspend fun pushUnsynced(): Boolean {
        val unsynced = localRepository.getUnsyncedWorkouts()
        if (unsynced.isEmpty()) return false

        val result = remoteRepository.saveWorkouts(unsynced)
        return when (val error = result.exceptionOrNull()) {
            null -> {
                result.getOrThrow().forEach { localRepository.updateWorkout(it, markUnsynced = false) }
                false
            }
            is IOException -> {
                Log.w(TAG, "Transient push failure: ${error.message}")
                true
            }
            else -> {
                Log.w(TAG, "Permanent push error: ${error.message}")
                unsynced.forEach {
                    localRepository.updateWorkout(
                        it.copy(workout = it.workout.copy(syncError = error.message)),
                        markUnsynced = false
                    )
                }
                false
            }
        }
    }

    private suspend fun pullRemote(): Boolean {
        val result = remoteRepository.getWorkouts()
        return when (val error = result.exceptionOrNull()) {
            null -> {
                val remoteWorkouts = result.getOrThrow()
                val localByRemoteId = localRepository.getWorkouts().getOrDefault(emptyList())
                    .filter { it.workout.remoteId != null }
                    .associateBy { it.workout.remoteId }

                remoteWorkouts.forEach { remote ->
                    val existingLocal = localByRemoteId[remote.workout.remoteId]
                    if (existingLocal != null) {
                        localRepository.updateWorkout(
                            remote.withLocalId(existingLocal.workout.id),
                            markUnsynced = false
                        )
                    } else {
                        localRepository.saveWorkout(remote, markUnsynced = false)
                    }
                }
                false
            }
            is IOException -> {
                Log.w(TAG, "Transient pull failure: ${error.message}")
                true
            }
            else -> {
                Log.w(TAG, "Permanent pull error: ${error.message}")
                false
            }
        }
    }

    private fun WorkoutWithDetails.withLocalId(localId: Long): WorkoutWithDetails = copy(
        workout = workout.copy(id = localId),
        runDetails = runDetails?.copy(workoutId = localId),
        bikeDetails = bikeDetails?.copy(workoutId = localId),
        skiDetails = skiDetails?.copy(workoutId = localId)
    )

    private suspend fun syncDeletes(): Boolean {
        val marked = localRepository.getWorkoutsMarkedForDeletion()
        var transient = false
        for (workout in marked) {
            val remoteId = workout.workout.remoteId
            if (remoteId == null) {
                localRepository.purgeWorkoutLocally(workout.workout.id)
                continue
            }
            val response = remoteRepository.deleteWorkoutRemotely(remoteId)
            transient = transient or when (val error = response.exceptionOrNull()) {
                null -> {
                    localRepository.purgeWorkoutLocally(workout.workout.id)
                    false
                }
                is IOException -> {
                    Log.w(TAG, "Transient delete failure for workout ${workout.workout.id}: ${error.message}")
                    true
                }
                else -> {
                    // 404/4xx: считаем за «уже удалено» и чистим локалку, чтобы не зацикливаться.
                    Log.w(TAG, "Permanent delete error for workout ${workout.workout.id}: ${error.message} — purging locally")
                    localRepository.purgeWorkoutLocally(workout.workout.id)
                    false
                }
            }
        }
        return transient
    }

    companion object {
        private const val TAG = "WorkoutSyncOrchestrator"
    }
}
