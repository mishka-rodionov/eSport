package com.competra.app.fcm

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.competra.domain.repository.fcm.FcmTokenRegistry
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * Реализация [FcmTokenRegistry] поверх WorkManager + Firebase SDK.
 *
 * **Назначение.** Связывает FCM-токен устройства с авторизованным пользователем на backend.
 * Все сетевые операции выполняются не напрямую, а через [FcmTokenWorker] — это даёт ретраи
 * по сети «из коробки» и переживает закрытие процесса приложения.
 *
 * **Три точки входа:**
 *  - [submit] — токен уже на руках (вызывается из [CompetraMessagingService.onNewToken],
 *    когда FCM прислал новый токен жизненным циклом самого SDK). Не делает сетевых вызовов
 *    сразу, ставит работу в очередь и возвращает управление.
 *  - [refresh] — токена на руках нет: сначала запрашиваем актуальный токен у Firebase SDK
 *    через [FirebaseMessaging.getToken] (suspend через `Task.await()`), затем ставим
 *    регистрацию в очередь. Вызывается после успешного логина — токен, выданный до логина,
 *    тогда не имел JWT для авторизации на backend, и его нужно «дослать».
 *  - [unregisterCurrent] — то же, но enqueueит worker в режиме `unregister = true`,
 *    который дёрнет `DELETE /api/devices/fcm-token`. Вызывается до очистки JWT при logout,
 *    чтобы вышедший пользователь не продолжал получать push.
 *
 * **Гарантии и поведение WorkManager:**
 *  - `Constraints` требуют сеть `CONNECTED` — без сети WorkManager сам отложит запуск.
 *  - `BackoffPolicy.EXPONENTIAL` с базой 30 секунд — при transient-ошибках сети worker
 *    автоматически повторяется с растущим интервалом.
 *  - `enqueueUniqueWork` + `ExistingWorkPolicy.REPLACE` исключают «параллельные» регистрации
 *    одного и того же токена: если за короткое время пришло два onNewToken с одним токеном,
 *    останется только последняя попытка. Имя уникальности строится из `hashCode()` токена
 *    (полный токен слишком длинный для ключа WorkManager), отдельно для register и unregister
 *    — параллельные ветки не вытесняют друг друга.
 *
 * **Что НЕ делает этот класс:**
 *  - Не хранит токен локально: единственным источником истины остаётся Firebase SDK.
 *  - Не отслеживает успех/неуспех отправки — это зона ответственности [FcmTokenWorker].
 *  - Не знает про JWT: авторизация подмешивается `AuthInteractor` через Retrofit-interceptor.
 *
 * **Жизненный цикл зависимостей:**
 *  - [context] — Application context (Koin `androidContext()`), переживает все Activity.
 *  - [firebaseMessaging] — синглтон `FirebaseMessaging.getInstance()` из [firebaseModule].
 */

    override fun submit(token: String) {
        enqueue(token, unregister = false)
    }

    override suspend fun refresh() {
        val token = firebaseMessaging.token.await()
        enqueue(token, unregister = false)
    }

    override suspend fun unregisterCurrent() {
        val token = firebaseMessaging.token.await()
        enqueue(token, unregister = true)
    }

    private fun enqueue(token: String, unregister: Boolean) {
        val request = OneTimeWorkRequestBuilder<FcmTokenWorker>()
            .setInputData(
                workDataOf(
                    FcmTokenWorker.KEY_TOKEN to token,
                    FcmTokenWorker.KEY_UNREGISTER to unregister,
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueName(token, unregister),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private fun uniqueName(token: String, unregister: Boolean): String =
        if (unregister) "fcm_token_unregister_${token.hashCode()}"
        else "fcm_token_register_${token.hashCode()}"

    private companion object {
        const val BACKOFF_SECONDS = 30L
    }
}
