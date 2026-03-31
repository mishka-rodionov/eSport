package com.rodionov.domain.repository

import com.rodionov.domain.models.orienteering.ResultConflictEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Синглтон-репозиторий для передачи события конфликта результата
 * от [feature:center] к [app] через SharedFlow.
 */
class ResultConflictRepository {
    private val _events = MutableSharedFlow<ResultConflictEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ResultConflictEvent> = _events.asSharedFlow()

    suspend fun emit(event: ResultConflictEvent) = _events.emit(event)
}
