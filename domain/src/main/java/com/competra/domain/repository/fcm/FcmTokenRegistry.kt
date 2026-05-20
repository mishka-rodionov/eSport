package com.competra.domain.repository.fcm

/**
 * Регистрация FCM-токена устройства на backend.
 *
 * - [submit] вызывается из `FirebaseMessagingService.onNewToken`, когда токен уже на руках.
 * - [refresh] получает текущий токен из Firebase SDK и отправляет на backend
 *   (использовать после успешного логина).
 * - [unregisterCurrent] получает текущий токен и удаляет привязку на backend
 *   (использовать перед logout).
 */
interface FcmTokenRegistry {
    fun submit(token: String)
    suspend fun refresh()
    suspend fun unregisterCurrent()
}
