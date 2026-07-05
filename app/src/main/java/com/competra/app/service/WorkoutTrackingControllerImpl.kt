package com.competra.app.service

import com.competra.domain.models.diary.SportType
import com.competra.ui.WorkoutTrackingCommand
import com.competra.ui.WorkoutTrackingController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Реализация контроллера управления сервисом live-трекинга тренировки.
 * Передаёт команды старт/пауза/резюм/стоп в MainActivity через SharedFlow
 * (по образцу [CompetitionServiceControllerImpl]).
 */
class WorkoutTrackingControllerImpl : WorkoutTrackingController {
    private val _commands = MutableSharedFlow<WorkoutTrackingCommand>(extraBufferCapacity = 1)
    override val commands: SharedFlow<WorkoutTrackingCommand> = _commands.asSharedFlow()

    override suspend fun start(sportType: SportType) {
        _commands.emit(WorkoutTrackingCommand.Start(sportType))
    }

    override suspend fun pause() {
        _commands.emit(WorkoutTrackingCommand.Pause)
    }

    override suspend fun resume() {
        _commands.emit(WorkoutTrackingCommand.Resume)
    }

    override suspend fun stop(discard: Boolean) {
        _commands.emit(WorkoutTrackingCommand.Stop(discard))
    }
}
