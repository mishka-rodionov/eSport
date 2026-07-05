package com.competra.diary.data.list

import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.ui.BaseAction

/** Действия на экране списка тренировок. */
sealed class DiaryListAction : BaseAction {

    /** Открыть форму создания новой тренировки. */
    data object OpenCreateWorkout : DiaryListAction()

    /** Открыть карточку тренировки. */
    data class OpenWorkoutDetails(val workout: WorkoutWithDetails) : DiaryListAction()

    /** Показать диалог подтверждения удаления. */
    data class ShowDeleteDialog(val workout: WorkoutWithDetails) : DiaryListAction()

    /** Скрыть диалог подтверждения удаления. */
    data object HideDeleteDialog : DiaryListAction()

    /** Удалить тренировку (soft-delete, выгрузится на сервер фоновым синком). */
    data class DeleteWorkout(val workout: WorkoutWithDetails) : DiaryListAction()

    /** Открыть выбор вида спорта перед стартом live-трекинга. */
    data object OpenSportPicker : DiaryListAction()

    /** Скрыть выбор вида спорта. */
    data object HideSportPicker : DiaryListAction()

    /** Выбран вид спорта — перейти на экран live-трекинга и запустить сервис. */
    data class StartTracking(val sportType: SportType) : DiaryListAction()

    /** Подключиться к уже идущей live-тренировке (сервис жив). */
    data object OpenInProgressWorkout : DiaryListAction()

    /** Завершить осиротевшую live-тренировку вручную (сервис мёртв, данные — из чек-пойнта). */
    data object FinalizeInProgressWorkout : DiaryListAction()
}
