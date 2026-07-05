package com.competra.local.entities.diary

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts",
    indices = [
        Index(name = "idx_workouts_unsynced", value = ["isSynced"])
    ]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: Long? = null,
    val sportType: String,
    val status: String,
    val scheduledDate: Long? = null,
    val startedAt: Long? = null,
    val durationSeconds: Int? = null,
    val distanceMeters: Int? = null,
    val elevationGainMeters: Int? = null,
    val notes: String? = null,
    val trackEncoded: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val serverUpdatedAt: Long? = null,
    val syncError: String? = null
)
