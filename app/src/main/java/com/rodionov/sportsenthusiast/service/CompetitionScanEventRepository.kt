package com.rodionov.sportsenthusiast.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Репозиторий для передачи событий NFC-сканирования от сервиса к UI.
 */
class CompetitionScanEventRepository {
    private val _events = MutableSharedFlow<NfcScanEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NfcScanEvent> = _events.asSharedFlow()

    suspend fun emit(event: NfcScanEvent) = _events.emit(event)
}
