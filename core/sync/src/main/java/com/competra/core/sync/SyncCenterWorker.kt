package com.competra.core.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.IOException

/**
 * WorkManager-обёртка над [SyncOrchestrator].
 *
 *  - [Result.success] — все записи успешно выгружены
 *  - [Result.retry]   — частичный успех (Outcome.Partial) или transient ошибка (IOException, 5xx).
 *    WorkManager применит ExponentialBackoff и повторит попытку.
 *  - [Result.failure] — нерекуперируемая ошибка (всё остальное).
 */
class SyncCenterWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val orchestrator: SyncOrchestrator
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            when (orchestrator.syncAll()) {
                SyncOrchestrator.Outcome.AllDone -> Result.success()
                SyncOrchestrator.Outcome.Partial -> Result.retry()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Transient failure, will retry: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Permanent failure", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SyncCenterWorker"
    }
}
