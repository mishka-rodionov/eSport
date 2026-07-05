package com.competra.diary.data.list

import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseState

/**
 * Состояние экрана списка тренировок дневника.
 *
 * @property workouts Все тренировки (запланированные и выполненные), отсортированные по дате.
 * @property isLoading Флаг только самой первой загрузки (спиннер на весь экран). Последующие
 *   фоновые перезагрузки (после сохранения/удаления/возврата на экран) его не трогают — список
 *   обновляется без промежуточного исчезновения контента.
 * @property deletingWorkout Тренировка, для которой показан диалог подтверждения удаления.
 */
data class DiaryListState(
    val workouts: List<WorkoutWithDetails> = emptyList(),
    val isLoading: Boolean = true,
    val deletingWorkout: WorkoutWithDetails? = null
) : BaseState
