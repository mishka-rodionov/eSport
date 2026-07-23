package com.competra.diary.data.tracking

import com.competra.domain.diary.TrackPoint
import com.competra.domain.models.diary.SportType
import com.competra.ui.WorkoutTrackingStatus
import com.competra.ui.BaseState

/** Состояние экрана live-трекинга тренировки. */
data class LiveTrackingState(
    val sportType: SportType = SportType.RUNNING,
    /** null — ещё не подключились к сервису (сразу после открытия экрана). */
    val status: WorkoutTrackingStatus? = null,
    val elapsedSeconds: Int = 0,
    val distanceMeters: Int = 0,
    val elevationGainMeters: Int = 0,
    val speedMetersPerSecond: Double = 0.0,
    val points: List<TrackPoint> = emptyList(),
    val isStopDialogVisible: Boolean = false
) : BaseState
