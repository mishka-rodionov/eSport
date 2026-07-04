package com.competra.diary.data.list

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseState

/**
 * Состояние экрана списка тренировок дневника.
 *
 * @property workouts Все тренировки (запланированные и выполненные), отсортированные по дате.
 * @property isLoading Флаг первичной загрузки.
 * @property deletingWorkout Тренировка, для которой показан диалог подтверждения удаления.
 */
data class DiaryListState(
    val workouts: List<WorkoutWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val deletingWorkout: WorkoutWithDetails? = null
) : BaseState
