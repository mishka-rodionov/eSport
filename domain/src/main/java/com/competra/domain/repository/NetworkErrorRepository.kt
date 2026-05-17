package com.competra.domain.repository

import com.competra.domain.models.NetworkErrorEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Синглтон-репозиторий для передачи событий сетевых ошибок
 * из feature-модулей в [app] через SharedFlow.
 */
class NetworkErrorRepository {
    private val _events = MutableSharedFlow<NetworkErrorEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NetworkErrorEvent> = _events.asSharedFlow()

    suspend fun emit(event: NetworkErrorEvent) = _events.emit(event)
}
