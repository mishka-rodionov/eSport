package com.competra.diary.data.editor

import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.ui.BaseAction

/** Действия на экране создания/редактирования тренировки. */
sealed class WorkoutEditorAction : BaseAction {
    data class SelectSportType(val sportType: SportType) : WorkoutEditorAction()
    data class SelectStatus(val status: WorkoutStatus) : WorkoutEditorAction()
    data class ChangeDate(val dateMillis: Long) : WorkoutEditorAction()
    data class ChangeDuration(val value: String) : WorkoutEditorAction()
    data class ChangeDistance(val value: String) : WorkoutEditorAction()
    data class ChangeElevationGain(val value: String) : WorkoutEditorAction()
    data class ChangeNotes(val value: String) : WorkoutEditorAction()
    data class ChangeCadenceSpm(val value: String) : WorkoutEditorAction()
    data class ChangeCadenceRpm(val value: String) : WorkoutEditorAction()
    data class ChangePowerWatts(val value: String) : WorkoutEditorAction()
    data class SelectSkiStyle(val style: SkiStyle) : WorkoutEditorAction()
    data object Save : WorkoutEditorAction()
}
