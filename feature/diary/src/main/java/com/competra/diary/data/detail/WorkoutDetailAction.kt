package com.competra.diary.data.detail

import com.competra.ui.BaseAction

sealed class WorkoutDetailAction : BaseAction {
    data object EditClick : WorkoutDetailAction()
    data object DeleteClick : WorkoutDetailAction()
    data object BackClick : WorkoutDetailAction()
}
