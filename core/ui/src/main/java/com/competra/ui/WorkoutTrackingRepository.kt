package com.competra.ui

import com.competra.domain.diary.TrackPoint
import com.competra.domain.models.diary.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class WorkoutTrackingStatus { RUNNING, PAUSED }

/** Живой снимок активной live-тренировки. */
data class WorkoutTrackingSnapshot(
    val status: WorkoutTrackingStatus,
    val sportType: SportType,
    val elapsedSeconds: Int,
    val distanceMeters: Int,
    val elevationGainMeters: Int,
    val points: List<TrackPoint>
)

/**
 * Передаёт живое состояние трекинга от сервиса к UI (по образцу [CompetitionStartTimeRepository]).
 * `null` — активной сессии нет.
 */
class WorkoutTrackingRepository {
    private val _snapshot = MutableStateFlow<WorkoutTrackingSnapshot?>(null)
    val snapshot: StateFlow<WorkoutTrackingSnapshot?> = _snapshot.asStateFlow()

    fun update(snapshot: WorkoutTrackingSnapshot) {
        _snapshot.value = snapshot
    }

    fun clear() {
        _snapshot.value = null
    }
}
