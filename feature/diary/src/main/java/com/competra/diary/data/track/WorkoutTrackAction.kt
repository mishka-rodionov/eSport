package com.competra.diary.data.track

import com.competra.ui.BaseAction

/** Действия на экране полноэкранного просмотра трека тренировки. */
sealed class WorkoutTrackAction : BaseAction {
    data object BackClick : WorkoutTrackAction()

    /** Экспортировать трек в GPX и открыть системный диалог "Поделиться". */
    data object ExportGpxClick : WorkoutTrackAction()
}
