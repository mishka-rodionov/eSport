package com.rodionov.sportsenthusiast.service

import com.rodionov.ui.CompetitionServiceCommand
import com.rodionov.ui.CompetitionServiceController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Реализация контроллера управления сервисом соревнования.
 * Передаёт команды запуска/остановки в MainActivity через SharedFlow.
 */
class CompetitionServiceControllerImpl : CompetitionServiceController {
    private val _commands = MutableSharedFlow<CompetitionServiceCommand>(extraBufferCapacity = 1)
    override val commands: SharedFlow<CompetitionServiceCommand> = _commands.asSharedFlow()

    override suspend fun start(competitionId: Long, startTimeMs: Long) {
        _commands.emit(CompetitionServiceCommand.Start(competitionId, startTimeMs))
    }

    override suspend fun stop() {
        _commands.emit(CompetitionServiceCommand.Stop)
    }
}
