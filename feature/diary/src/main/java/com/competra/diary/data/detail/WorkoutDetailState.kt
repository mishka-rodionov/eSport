package com.competra.diary.data.detail

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseState

data class WorkoutDetailState(
    val workout: WorkoutWithDetails? = null
) : BaseState
