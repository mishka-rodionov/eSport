package com.competra.diary.presentation.list

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.DiaryNavigation
import com.competra.data.navigation.Navigation
import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.diary.data.list.DiaryListAction
import com.competra.diary.data.list.DiaryListState
import com.competra.diary.presentation.editor.toAnalytics
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel экрана списка тренировок дневника. */
class DiaryListViewModel(
    private val interactor: WorkoutInteractor,
    private val navigation: Navigation,
    private val analytics: AnalyticsTracker
) : BaseViewModel<DiaryListState>(DiaryListState()) {

    init {
        loadWorkouts()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            DiaryListAction.OpenCreateWorkout -> viewModelScope.launch {
                navigation.navigate(DiaryNavigation.WorkoutEditorRoute())
            }
            is DiaryListAction.OpenWorkoutDetails -> viewModelScope.launch {
                navigation.navigate(DiaryNavigation.WorkoutDetailRoute(action.workout.workout.id))
            }
            is DiaryListAction.ShowDeleteDialog -> updateState { copy(deletingWorkout = action.workout) }
            DiaryListAction.HideDeleteDialog -> updateState { copy(deletingWorkout = null) }
            is DiaryListAction.DeleteWorkout -> {
                updateState { copy(deletingWorkout = null) }
                viewModelScope.launch(Dispatchers.IO) {
                    interactor.deleteWorkout(action.workout.workout.id).onSuccess {
                        analytics.trackEvent(
                            AnalyticsEvent.DiaryWorkoutDeleted(action.workout.workout.sportType.toAnalytics())
                        )
                    }
                    loadWorkouts()
                }
            }
        }
    }

    fun loadWorkouts() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getWorkouts()
                .onSuccess { list -> updateState { copy(workouts = list, isLoading = false) } }
                .onFailure { updateState { copy(isLoading = false) } }
        }
    }
}
