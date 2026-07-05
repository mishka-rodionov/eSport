package com.competra.diary.data.editor

import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.ui.BaseState

/**
 * Состояние формы создания/редактирования тренировки.
 *
 * Числовые поля хранятся как сырой пользовательский ввод (String), чтобы не терять
 * промежуточные некорректные значения при вводе — валидация/парсинг происходит при сохранении.
 */
data class WorkoutEditorState(
    val workoutId: Long? = null,
    val sportType: SportType = SportType.RUNNING,
    val status: WorkoutStatus = WorkoutStatus.COMPLETED,
    val dateMillis: Long = System.currentTimeMillis(),
    val durationHoursInput: String = "",
    val durationMinutesInput: String = "",
    val durationSecondsInput: String = "",
    val distanceKmInput: String = "",
    val elevationGainInput: String = "",
    val notes: String = "",
    val cadenceSpmInput: String = "",
    val cadenceRpmInput: String = "",
    val powerWattsInput: String = "",
    val skiStyle: SkiStyle = SkiStyle.CLASSIC,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) : BaseState

/**
 * Суммирует часы/минуты/секунды в общее число секунд. Незаполненное поле трактуется как 0.
 * Если пользователь вообще не тронул ни одно из трёх полей — возвращает null (длительность
 * не указана), а не 0.
 */
fun WorkoutEditorState.totalDurationSeconds(): Int? {
    if (durationHoursInput.isBlank() && durationMinutesInput.isBlank() && durationSecondsInput.isBlank()) {
        return null
    }
    val hours = durationHoursInput.toIntOrNull() ?: 0
    val minutes = durationMinutesInput.toIntOrNull() ?: 0
    val seconds = durationSecondsInput.toIntOrNull() ?: 0
    return hours * 3600 + minutes * 60 + seconds
}
