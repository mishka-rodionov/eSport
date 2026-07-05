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
 * @property isSportPickerVisible Показан ли выбор вида спорта перед стартом live-трекинга.
 * @property inProgressWorkout Незавершённая live-тренировка, если есть (исключена из [workouts]).
 * @property isTrackingAlive true — сервис трекинга ещё жив (можно подключиться), false — сервис
 *   мёртв (например, приложение перезапустили) и [inProgressWorkout] можно только завершить вручную.
 */
data class DiaryListState(
    val workouts: List<WorkoutWithDetails> = emptyList(),
    val isLoading: Boolean = true,
    val deletingWorkout: WorkoutWithDetails? = null,
    val isSportPickerVisible: Boolean = false,
    val inProgressWorkout: WorkoutWithDetails? = null,
    val isTrackingAlive: Boolean = false
) : BaseState
