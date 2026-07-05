package com.competra.diary.presentation.track

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.diary.data.track.WorkoutTrackAction
import com.competra.diary.data.track.WorkoutTrackState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel полноэкранного просмотра трека тренировки. */
class WorkoutTrackViewModel(
    private val interactor: WorkoutInteractor,
    private val navigation: Navigation
) : BaseViewModel<WorkoutTrackState>(WorkoutTrackState()) {

    fun load(workoutId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getWorkoutById(workoutId).onSuccess { updateState { copy(workout = it) } }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            WorkoutTrackAction.BackClick -> viewModelScope.launch { navigation.back() }
        }
    }
}
