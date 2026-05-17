package com.competra.core.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Помещает [SyncCenterWorker] в очередь WorkManager как UniqueWork с политикой KEEP.
 *
 * Гарантии:
 * - Не плодит дубликаты — если предыдущий запуск ещё running/enqueued, новый игнорируется.
 * - Constraint NetworkType.CONNECTED — система сама отложит выполнение, если сети нет.
 * - ExponentialBackoff с базой 30 секунд для transient ошибок.
 */
object SyncBootstrap {

    private const val UNIQUE_WORK_NAME = "SyncCenterWorker"
    private const val BACKOFF_BASE_SECONDS = 30L

    fun enqueue(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncCenterWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_BASE_SECONDS,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
