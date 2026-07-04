package com.competra.local.entities.diary

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "run_details",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId", unique = true)]
)
data class RunDetailsEntity(
    @PrimaryKey
    val workoutId: Long,
    val cadenceSpm: Int? = null
)

@Entity(
    tableName = "bike_details",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId", unique = true)]
)
data class BikeDetailsEntity(
    @PrimaryKey
    val workoutId: Long,
    val cadenceRpm: Int? = null,
    val powerWatts: Int? = null
)

@Entity(
    tableName = "ski_details",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId", unique = true)]
)
data class SkiDetailsEntity(
    @PrimaryKey
    val workoutId: Long,
    val style: String
)
