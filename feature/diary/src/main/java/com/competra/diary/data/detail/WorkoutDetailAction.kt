package com.competra.diary.data.detail

import com.competra.ui.BaseAction

sealed class WorkoutDetailAction : BaseAction {
    data object EditClick : WorkoutDetailAction()
    data object DeleteClick : WorkoutDetailAction()
    data object ConfirmDeleteClick : WorkoutDetailAction()
    data object CancelDeleteClick : WorkoutDetailAction()
    data object BackClick : WorkoutDetailAction()

    /** Открыть полноэкранный просмотр записанного трека. */
    data object ViewTrackClick : WorkoutDetailAction()
}
