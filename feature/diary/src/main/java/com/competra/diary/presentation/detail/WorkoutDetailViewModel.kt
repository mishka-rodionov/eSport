package com.competra.diary.presentation.detail

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.DiaryNavigation
import com.competra.data.navigation.Navigation
import com.competra.diary.data.detail.WorkoutDetailAction
import com.competra.diary.data.detail.WorkoutDetailState
import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.diary.presentation.editor.toAnalytics
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel карточки одной тренировки. */
class WorkoutDetailViewModel(
    private val interactor: WorkoutInteractor,
    private val navigation: Navigation,
    private val analytics: AnalyticsTracker
) : BaseViewModel<WorkoutDetailState>(WorkoutDetailState()) {

    fun load(workoutId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getWorkoutById(workoutId).onSuccess { updateState { copy(workout = it) } }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            WorkoutDetailAction.EditClick -> {
                val workoutId = stateValue.workout?.workout?.id ?: return
                viewModelScope.launch { navigation.navigate(DiaryNavigation.WorkoutEditorRoute(workoutId)) }
            }
            WorkoutDetailAction.DeleteClick -> {
                val workout = stateValue.workout?.workout ?: return
                viewModelScope.launch(Dispatchers.IO) {
                    interactor.deleteWorkout(workout.id).onSuccess {
                        analytics.trackEvent(AnalyticsEvent.DiaryWorkoutDeleted(workout.sportType.toAnalytics()))
                    }
                    navigation.back()
                }
            }
            WorkoutDetailAction.BackClick -> viewModelScope.launch { navigation.back() }
        }
    }
}
