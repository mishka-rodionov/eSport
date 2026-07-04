package com.competra.core.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.IOException

/**
 * WorkManager-обёртка над [WorkoutSyncOrchestrator].
 *
 *  - [Result.success] — все записи успешно выгружены/подтянуты
 *  - [Result.retry]   — transient ошибка (IOException, 5xx). WorkManager применит ExponentialBackoff.
 *  - [Result.failure] — нерекуперируемая ошибка.
 */
class WorkoutSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val orchestrator: WorkoutSyncOrchestrator
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            orchestrator.syncAll()
            Result.success()
        } catch (e: IOException) {
            Log.w(TAG, "Transient failure, will retry: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Permanent failure", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "WorkoutSyncWorker"
    }
}
