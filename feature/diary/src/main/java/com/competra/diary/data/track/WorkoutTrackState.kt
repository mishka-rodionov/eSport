package com.competra.diary.data.track

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseState

/** Состояние экрана полноэкранного просмотра трека тренировки. */
data class WorkoutTrackState(
    val workout: WorkoutWithDetails? = null
) : BaseState
