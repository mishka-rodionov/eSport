package com.competra.app.fcm

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.competra.app.BuildConfig
import com.competra.domain.repository.fcm.FcmTokenRegistry
import com.competra.remote.datasource.device.DeviceRemoteDataSource
import com.competra.remote.request.device.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * Реализация [FcmTokenRegistry] поверх WorkManager + Firebase SDK.
 *
 * **Назначение.** Связывает FCM-токен устройства с авторизованным пользователем на backend.
 *
 * **Точки входа:**
 *  - [submit] — токен уже на руках (вызов из [CompetraMessagingService.onNewToken]).
 *    Ставит [FcmTokenWorker] в очередь, возвращает управление. Если на момент исполнения
 *    worker-а JWT ещё нет (юзер не залогинен), worker корректно завершится «нечего делать».
 *  - [refresh] — токена на руках нет: сначала достаём актуальный токен у Firebase SDK
 *    через `FirebaseMessaging.token.await()`, потом enqueue. Вызывается после успешного
 *    логина — токен, полученный до логина, тогда не имел JWT и не зарегистрировался.
 *  - [unregisterCurrent] — **синхронный** прямой вызов backend без WorkManager. Это
 *    намеренно: logout-флоу очищает JWT сразу после `unregisterCurrent`, поэтому
 *    отложенный worker не успел бы воспользоваться валидным заголовком авторизации.
 *    Сетевые ошибки при logout логируются и проглатываются — пользователь должен выйти
 *    в любом случае, даже без подтверждения от сервера.
 *
 * **Поведение WorkManager при register/refresh:**
 *  - `Constraints` требуют сеть `CONNECTED` — без сети WorkManager сам отложит запуск.
 *  - `BackoffPolicy.EXPONENTIAL` с базой 30 секунд — на transient-ошибках сети.
 *  - `enqueueUniqueWork` + `REPLACE` исключает параллельные регистрации одного токена.
 */
class FcmTokenRegistryImpl(
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
    private val deviceApi: DeviceRemoteDataSource,
) : FcmTokenRegistry {

    override fun submit(token: String) {
        enqueueRegistration(token)
    }

    override suspend fun refresh() {
        val token = firebaseMessaging.token.await()
        enqueueRegistration(token)
    }

    override suspend fun unregisterCurrent() {
        val token = runCatching { firebaseMessaging.token.await() }.getOrNull()
        if (token.isNullOrBlank()) {
            Log.w(TAG, "No FCM token available to unregister")
            return
        }
        val request = FcmTokenRequest(token = token, appVersion = BuildConfig.VERSION_NAME)
        deviceApi.unregisterFcmToken(request)
            .onFailure { Log.w(TAG, "Unregister failed (proceeding with logout anyway): ${it.message}") }
    }

    private fun enqueueRegistration(token: String) {
        val request = OneTimeWorkRequestBuilder<FcmTokenWorker>()
            .setInputData(workDataOf(FcmTokenWorker.KEY_TOKEN to token))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "fcm_token_register_${token.hashCode()}",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private companion object {
        const val BACKOFF_SECONDS = 30L
        const val TAG = "FcmTokenRegistry"
    }
}
