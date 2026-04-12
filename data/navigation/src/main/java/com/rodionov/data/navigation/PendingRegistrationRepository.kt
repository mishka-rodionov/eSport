package com.rodionov.data.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Отложенное действие регистрации — сохраняется когда неавторизованный пользователь
 * пытается зарегистрироваться, чтобы выполнить его после успешной авторизации.
 *
 * @property eventId Идентификатор соревнования.
 * @property groupId Идентификатор группы (null = пользователь ещё не выбрал группу).
 */
data class PendingRegistration(
    val eventId: Long,
    val groupId: String? = null
)

/**
 * In-memory хранилище отложенного действия регистрации.
 * Регистрируется как singleton в Koin.
 */
class PendingRegistrationRepository {

    private val _pending = MutableStateFlow<PendingRegistration?>(null)

    /** Текущее отложенное действие регистрации. */
    val pending: StateFlow<PendingRegistration?> = _pending.asStateFlow()

    /** Сохраняет отложенное действие регистрации. */
    fun set(eventId: Long, groupId: String? = null) {
        _pending.value = PendingRegistration(eventId, groupId)
    }

    /** Очищает отложенное действие. */
    fun clear() {
        _pending.value = null
    }
}
