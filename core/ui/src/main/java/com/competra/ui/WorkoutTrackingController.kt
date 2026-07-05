package com.competra.ui

import com.competra.domain.models.diary.SportType
import kotlinx.coroutines.flow.SharedFlow

/**
 * Интерфейс для управления foreground-сервисом live-трекинга тренировки.
 * Используется из `:feature:diary` для старта/паузы/остановки без прямой зависимости
 * на Android-специфичные API (по образцу [CompetitionServiceController]).
 */
interface WorkoutTrackingController {
    val commands: SharedFlow<WorkoutTrackingCommand>
    suspend fun start(sportType: SportType)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop(discard: Boolean = false)
}

sealed class WorkoutTrackingCommand {
    data class Start(val sportType: SportType) : WorkoutTrackingCommand()
    data object Pause : WorkoutTrackingCommand()
    data object Resume : WorkoutTrackingCommand()
    data class Stop(val discard: Boolean = false) : WorkoutTrackingCommand()
}
