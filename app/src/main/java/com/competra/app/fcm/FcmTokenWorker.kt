package com.competra.app.fcm

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.competra.app.BuildConfig
import com.competra.remote.datasource.device.DeviceRemoteDataSource
import com.competra.remote.request.device.FcmTokenRequest
import java.io.IOException

/**
 * Отправляет FCM-токен на backend (или удаляет его при logout).
 *
 * Ошибки сети → retry с экспоненциальным бэкоффом (см. [FcmTokenRegistryImpl]).
 * 4xx (кроме 5xx/IO) → failure: бессмысленно ретраить, пока нет валидного JWT
 * (interceptor не подставил токен, или backend отверг по другой причине).
 */
class FcmTokenWorker(
    appContext: Context,
    params: WorkerParameters,
    private val api: DeviceRemoteDataSource,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val token = inputData.getString(KEY_TOKEN) ?: return Result.failure()
        val unregister = inputData.getBoolean(KEY_UNREGISTER, false)
        val request = FcmTokenRequest(token = token, appVersion = BuildConfig.VERSION_NAME)
        return try {
            val result = if (unregister) api.unregisterFcmToken(request) else api.registerFcmToken(request)
            result.fold(
                onSuccess = { Result.success() },
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
            Log.e(TAG, "Server rejected FCM token request", t)
            Result.failure()
        }
    }

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_UNREGISTER = "unregister"
        private const val TAG = "FcmTokenWorker"
    }
}
