package com.competra.diary.presentation.editor

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.Navigation
import com.competra.diary.data.editor.WorkoutEditorAction
import com.competra.diary.data.editor.WorkoutEditorState
import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.domain.models.diary.BikeDetails
import com.competra.domain.models.diary.RunDetails
import com.competra.domain.models.diary.SkiDetails
import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.Workout
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel формы создания/редактирования тренировки (ручной ввод и планирование). */
class WorkoutEditorViewModel(
    private val interactor: WorkoutInteractor,
    private val navigation: Navigation,
    private val analytics: AnalyticsTracker
) : BaseViewModel<WorkoutEditorState>(WorkoutEditorState()) {

    /** Загружает существующую тренировку для редактирования. При null — форма создания. */
    fun load(workoutId: Long?) {
        if (workoutId == null) {
            updateState { WorkoutEditorState() }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getWorkoutById(workoutId).onSuccess { existing ->
                val details = existing ?: return@onSuccess
                updateState {
                    WorkoutEditorState(
                        workoutId = details.workout.id,
                        sportType = details.workout.sportType,
                        status = details.workout.status,
                        dateMillis = details.workout.startedAt
                            ?: details.workout.scheduledDate
                            ?: System.currentTimeMillis(),
                        durationMinutesInput = details.workout.durationSeconds?.let { (it / 60).toString() }.orEmpty(),
                        distanceKmInput = details.workout.distanceMeters?.let { (it / 1000.0).toString() }.orEmpty(),
                        elevationGainInput = details.workout.elevationGainMeters?.toString().orEmpty(),
                        notes = details.workout.notes.orEmpty(),
                        cadenceSpmInput = details.runDetails?.cadenceSpm?.toString().orEmpty(),
                        cadenceRpmInput = details.bikeDetails?.cadenceRpm?.toString().orEmpty(),
                        powerWattsInput = details.bikeDetails?.powerWatts?.toString().orEmpty(),
                        skiStyle = details.skiDetails?.style ?: SkiStyle.CLASSIC
                    )
                }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is WorkoutEditorAction.SelectSportType -> updateState { copy(sportType = action.sportType) }
            is WorkoutEditorAction.SelectStatus -> updateState { copy(status = action.status) }
            is WorkoutEditorAction.ChangeDate -> updateState { copy(dateMillis = action.dateMillis) }
            is WorkoutEditorAction.ChangeDuration -> updateState { copy(durationMinutesInput = action.value) }
            is WorkoutEditorAction.ChangeDistance -> updateState { copy(distanceKmInput = action.value) }
            is WorkoutEditorAction.ChangeElevationGain -> updateState { copy(elevationGainInput = action.value) }
            is WorkoutEditorAction.ChangeNotes -> updateState { copy(notes = action.value) }
            is WorkoutEditorAction.ChangeCadenceSpm -> updateState { copy(cadenceSpmInput = action.value) }
            is WorkoutEditorAction.ChangeCadenceRpm -> updateState { copy(cadenceRpmInput = action.value) }
            is WorkoutEditorAction.ChangePowerWatts -> updateState { copy(powerWattsInput = action.value) }
            is WorkoutEditorAction.SelectSkiStyle -> updateState { copy(skiStyle = action.style) }
            WorkoutEditorAction.Save -> save()
        }
    }

    private fun save() {
        val s = stateValue
        val isNew = s.workoutId == null
        updateState { copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val workout = Workout(
                id = s.workoutId ?: 0,
                sportType = s.sportType,
                status = s.status,
                scheduledDate = if (s.status == WorkoutStatus.PLANNED) s.dateMillis else null,
                startedAt = if (s.status == WorkoutStatus.COMPLETED) s.dateMillis else null,
                durationSeconds = s.durationMinutesInput.toIntOrNull()?.times(60),
                distanceMeters = s.distanceKmInput.toDoubleOrNull()?.times(1000)?.toInt(),
                elevationGainMeters = s.elevationGainInput.toIntOrNull(),
                notes = s.notes.ifBlank { null }
            )
            val workoutWithDetails = WorkoutWithDetails(
                workout = workout,
                runDetails = if (s.sportType == SportType.RUNNING) {
                    RunDetails(workoutId = workout.id, cadenceSpm = s.cadenceSpmInput.toIntOrNull())
                } else null,
                bikeDetails = if (s.sportType == SportType.CYCLING) {
                    BikeDetails(
                        workoutId = workout.id,
                        cadenceRpm = s.cadenceRpmInput.toIntOrNull(),
                        powerWatts = s.powerWattsInput.toIntOrNull()
                    )
                } else null,
                skiDetails = if (s.sportType == SportType.SKIING) {
                    SkiDetails(workoutId = workout.id, style = s.skiStyle)
                } else null
            )

            val result = if (s.workoutId == null) {
                interactor.saveWorkout(workoutWithDetails)
            } else {
                interactor.updateWorkout(workoutWithDetails)
            }

            result.onSuccess {
                analytics.trackEvent(
                    AnalyticsEvent.DiaryWorkoutSaved(
                        sportType = s.sportType.toAnalytics(),
                        status = s.status.toAnalytics(),
                        isNew = isNew
                    )
                )
                updateState { copy(isSaving = false, isSaved = true) }
                navigation.back()
            }.onFailure {
                updateState { copy(isSaving = false) }
            }
        }
    }
}

internal fun SportType.toAnalytics(): AnalyticsEvent.DiarySportType = when (this) {
    SportType.RUNNING -> AnalyticsEvent.DiarySportType.RUNNING
    SportType.CYCLING -> AnalyticsEvent.DiarySportType.CYCLING
    SportType.SKIING -> AnalyticsEvent.DiarySportType.SKIING
}

internal fun WorkoutStatus.toAnalytics(): AnalyticsEvent.DiaryWorkoutStatus = when (this) {
    WorkoutStatus.PLANNED -> AnalyticsEvent.DiaryWorkoutStatus.PLANNED
    WorkoutStatus.COMPLETED -> AnalyticsEvent.DiaryWorkoutStatus.COMPLETED
}
