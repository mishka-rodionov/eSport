package com.competra.local.entities.diary

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithDetailsEntity(
    @Embedded
    val workout: WorkoutEntity,

    @Relation(parentColumn = "id", entityColumn = "workoutId")
    val runDetails: RunDetailsEntity?,

    @Relation(parentColumn = "id", entityColumn = "workoutId")
    val bikeDetails: BikeDetailsEntity?,

    @Relation(parentColumn = "id", entityColumn = "workoutId")
    val skiDetails: SkiDetailsEntity?
)
