package com.rodionov.domain.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Репозиторий для управления состоянием загрузки во всем приложении.
 * Позволяет любому модулю инициировать показ или скрытие глобального лоадера.
 */
class LoadingRepository {
    private val _loadingEvents = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    /**
     * Поток событий состояния загрузки.
     * true - показать лоадер, false - скрыть.
     */
    val loadingEvents: SharedFlow<Boolean> = _loadingEvents.asSharedFlow()

    /**
     * Отправляет событие изменения состояния загрузки.
     *
     * @param isLoading Состояние загрузки (true для отображения, false для скрытия).
     */
    suspend fun emit(isLoading: Boolean) = _loadingEvents.emit(isLoading)
}
