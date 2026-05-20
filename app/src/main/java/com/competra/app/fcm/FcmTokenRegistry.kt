package com.competra.app.fcm

import android.util.Log

/**
 * Принимает свежий FCM-токен и отправляет его на backend.
 *
 * Пока endpoint не согласован — реализация только логирует токен.
 * После появления API заменить тело [submit] на вызов сервиса
 * (через WorkManager для ретраев при отсутствии сети).
 */
interface FcmTokenRegistry {
    fun submit(token: String)
}

class FcmTokenRegistryImpl : FcmTokenRegistry {
    override fun submit(token: String) {
        Log.d(TAG, "FCM token received (TODO: send to backend): $token")
    }

    private companion object {
        const val TAG = "FcmTokenRegistry"
    }
}
