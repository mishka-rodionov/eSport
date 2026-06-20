package com.competra.ui

import kotlinx.coroutines.flow.SharedFlow

/**
 * Интерфейс для управления foreground-сервисом соревнования.
 * Используется из feature-модулей для запуска и остановки сервиса
 * без прямой зависимости на Android-специфичные API.
 */
interface CompetitionServiceController {
    val commands: SharedFlow<CompetitionServiceCommand>
    suspend fun start(competitionId: String, startTimeMs: Long)
    suspend fun stop()
}

sealed class CompetitionServiceCommand {
    data class Start(val competitionId: String, val startTimeMs: Long) : CompetitionServiceCommand()
    data object Stop : CompetitionServiceCommand()
}
