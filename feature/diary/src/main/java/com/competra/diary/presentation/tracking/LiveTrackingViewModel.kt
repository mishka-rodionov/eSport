package com.competra.diary.presentation.tracking

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.diary.data.tracking.LiveTrackingAction
import com.competra.diary.data.tracking.LiveTrackingState
import com.competra.domain.models.diary.SportType
import com.competra.ui.BaseAction
import com.competra.ui.WorkoutTrackingController
import com.competra.ui.WorkoutTrackingRepository
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/** ViewModel экрана live-трекинга: старт/пауза/резюм/стоп + наблюдение за живым снапшотом сервиса. */
class LiveTrackingViewModel(
    private val controller: WorkoutTrackingController,
    private val trackingRepository: WorkoutTrackingRepository,
    private val navigation: Navigation
) : BaseViewModel<LiveTrackingState>(LiveTrackingState()) {

    init {
        viewModelScope.launch {
            trackingRepository.snapshot.collect { snapshot ->
                if (snapshot != null) {
                    updateState {
                        copy(
                            sportType = snapshot.sportType,
                            status = snapshot.status,
                            elapsedSeconds = snapshot.elapsedSeconds,
                            distanceMeters = snapshot.distanceMeters,
                            elevationGainMeters = snapshot.elevationGainMeters,
                            points = snapshot.points
                        )
                    }
                }
            }
        }
    }

    /**
     * Открывает сессию для [sportType]. Если сервис уже трекает (например, экран переоткрыт
     * после сворачивания приложения) — новую сессию не запускает, просто подключается
     * к уже идущей через [trackingRepository].
     */
    fun start(sportType: SportType) {
        if (trackingRepository.snapshot.value != null) return
        updateState { copy(sportType = sportType) }
        viewModelScope.launch { controller.start(sportType) }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            LiveTrackingAction.PauseClick -> viewModelScope.launch { controller.pause() }
            LiveTrackingAction.ResumeClick -> viewModelScope.launch { controller.resume() }
            LiveTrackingAction.StopClick -> updateState { copy(isStopDialogVisible = true) }
            LiveTrackingAction.CancelStopDialogClick -> updateState { copy(isStopDialogVisible = false) }
            LiveTrackingAction.ConfirmStopClick -> finish(discard = false)
            LiveTrackingAction.DiscardClick -> finish(discard = true)
        }
    }

    private fun finish(discard: Boolean) {
        updateState { copy(isStopDialogVisible = false) }
        viewModelScope.launch {
            controller.stop(discard)
            navigation.back()
        }
    }
}
