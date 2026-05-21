package com.competra.app.fcm

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.competra.app.BuildConfig
import com.competra.domain.repository.auth.TokenRepository
import com.competra.remote.datasource.device.DeviceRemoteDataSource
import com.competra.remote.request.device.FcmTokenRequest
import java.io.IOException

/**
 * Отправляет FCM-токен на backend.
 *
 * Жизненный цикл:
 *  - Если JWT в [TokenRepository] отсутствует — токен FCM не с кем связывать, work
 *    завершается успехом без сетевого вызова. После логина [AuthInteractor.authorize]
 *    дёрнет [FcmTokenRegistry.refresh], который поставит новый worker с уже валидным JWT.
 *  - `IOException` → [Result.retry] с экспоненциальным backoff из [FcmTokenRegistryImpl].
 *  - Прочие ошибки (4xx, 5xx, парсинг) → [Result.failure]: ретрай не поможет.
 *
 * Этот worker занимается только регистрацией. Отмена регистрации делается
 * синхронно в [FcmTokenRegistryImpl.unregisterCurrent], потому что к моменту
 * выполнения отложенного worker-а JWT может быть уже очищен (см. logout-флоу).
 */
class FcmTokenWorker(
    appContext: Context,
    params: WorkerParameters,
    private val api: DeviceRemoteDataSource,
    private val tokenRepository: TokenRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val token = inputData.getString(KEY_TOKEN) ?: return Result.failure()

        val accessToken = tokenRepository.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            Log.i(TAG, "No JWT yet — skipping FCM token registration until user logs in")
            return Result.success()
        }

        val request = FcmTokenRequest(token = token, appVersion = BuildConfig.VERSION_NAME)
        return try {
            api.registerFcmToken(request).fold(
                onSuccess = {
                    Log.i(TAG, "FCM token registered on backend")
                    Result.success()
                },
                onFailure = { handleFailure(it) },
            )
        } catch (e: IOException) {
            Log.w(TAG, "Transient error, will retry", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Permanent error", e)
            Result.failure()
        }
    }

    private fun handleFailure(t: Throwable): Result = when (t) {
        is IOException -> Result.retry()
        else -> {
            Log.e(TAG, "Server rejected FCM token registration: ${t.message?.ifBlank { "<empty body>" } ?: t.javaClass.simpleName}", t)
            Result.failure()
        }
    }

    companion object {
        const val KEY_TOKEN = "token"
        private const val TAG = "FcmTokenWorker"
    }
}
