package com.competra.diary.di

import com.competra.diary.data.interactors.WorkoutInteractor
import com.competra.diary.presentation.detail.WorkoutDetailViewModel
import com.competra.diary.presentation.editor.WorkoutEditorViewModel
import com.competra.diary.presentation.list.DiaryListViewModel
import com.competra.diary.presentation.tracking.LiveTrackingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryModule = module {
    factoryOf(::WorkoutInteractor)
    viewModelOf(::DiaryListViewModel)
    viewModelOf(::WorkoutEditorViewModel)
    viewModelOf(::WorkoutDetailViewModel)
    viewModelOf(::LiveTrackingViewModel)
}
