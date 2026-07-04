package com.competra.domain.models.diary

/** Статус тренировки в дневнике. */
enum class WorkoutStatus {
    /** Запланирована на будущую дату, ещё не выполнена. */
    PLANNED,

    /** Выполнена (записана постфактум вручную). */
    COMPLETED
}
