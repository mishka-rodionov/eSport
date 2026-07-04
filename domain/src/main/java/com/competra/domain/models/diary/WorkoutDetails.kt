package com.competra.domain.models.diary

/** Специфичные для бега поля тренировки. */
data class RunDetails(
    val workoutId: Long,
    val cadenceSpm: Int? = null
)

/** Специфичные для велотренировки поля. */
data class BikeDetails(
    val workoutId: Long,
    val cadenceRpm: Int? = null,
    val powerWatts: Int? = null
)

/** Специфичные для лыжной тренировки поля. */
data class SkiDetails(
    val workoutId: Long,
    val style: SkiStyle
)

/** Агрегат тренировки со специфичными для её вида спорта полями. */
data class WorkoutWithDetails(
    val workout: Workout,
    val runDetails: RunDetails? = null,
    val bikeDetails: BikeDetails? = null,
    val skiDetails: SkiDetails? = null
)
