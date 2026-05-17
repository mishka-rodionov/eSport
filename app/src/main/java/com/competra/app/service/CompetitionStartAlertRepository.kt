package com.competra.app.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Репозиторий для передачи предстартовых оповещений от сервиса к UI.
 */
class CompetitionStartAlertRepository {
    private val _events = MutableSharedFlow<ParticipantStartAlert>(extraBufferCapacity = 4)
    val events: SharedFlow<ParticipantStartAlert> = _events.asSharedFlow()

    suspend fun emit(event: ParticipantStartAlert) = _events.emit(event)
}
