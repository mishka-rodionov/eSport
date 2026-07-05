package com.competra.diary.presentation.track

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.diary.data.track.WorkoutTrackAction
import com.competra.diary.data.track.WorkoutTrackState
import com.competra.diary.presentation.common.GpxExporter
import com.competra.domain.diary.TrackCodec
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/** ViewModel полноэкранного просмотра трека тренировки. */
class WorkoutTrackViewModel(
    private val interactor: WorkoutInteractor,
    private val navigation: Navigation
) : BaseViewModel<WorkoutTrackState>(WorkoutTrackState()) {

    private val _exportGpxEvent = MutableSharedFlow<Pair<String, String>>()

    /** (имя файла, содержимое GPX) — экран пишет файл в кэш и открывает системный шаринг. */
    val exportGpxEvent: SharedFlow<Pair<String, String>> = _exportGpxEvent

    fun load(workoutId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getWorkoutById(workoutId).onSuccess { updateState { copy(workout = it) } }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            WorkoutTrackAction.BackClick -> viewModelScope.launch { navigation.back() }
            WorkoutTrackAction.ExportGpxClick -> exportGpx()
        }
    }

    private fun exportGpx() {
        val workout = stateValue.workout?.workout ?: return
        val encoded = workout.trackEncoded ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val points = TrackCodec.decode(workout.startedAt ?: 0L, encoded)
            if (points.isEmpty()) return@launch
            val content = GpxExporter.buildGpx(workout.sportType, points)
            val fileName = GpxExporter.fileName(workout.sportType, workout.startedAt)
            _exportGpxEvent.emit(fileName to content)
        }
    }
}
