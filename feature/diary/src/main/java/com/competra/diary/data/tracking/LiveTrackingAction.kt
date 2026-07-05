package com.competra.diary.data.tracking

import com.competra.ui.BaseAction

/** Действия на экране live-трекинга тренировки. */
sealed class LiveTrackingAction : BaseAction {
    data object PauseClick : LiveTrackingAction()
    data object ResumeClick : LiveTrackingAction()

    /** Открывает диалог подтверждения завершения (сохранить или отменить тренировку). */
    data object StopClick : LiveTrackingAction()
    data object CancelStopDialogClick : LiveTrackingAction()

    /** Завершить и сохранить тренировку. */
    data object ConfirmStopClick : LiveTrackingAction()

    /** Завершить и отменить тренировку без сохранения. */
    data object DiscardClick : LiveTrackingAction()
}
