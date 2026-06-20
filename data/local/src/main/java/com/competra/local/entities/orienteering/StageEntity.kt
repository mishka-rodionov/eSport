package com.competra.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.competra.domain.models.Competition

@Entity(
    tableName = "stages",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["competitionId"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("competitionId")]
)
data class StageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: Long? = null,
    val competitionId: String,
    val stageNumber: Int,
    val startDate: Long,
    val endDate: Long? = null,
    val description: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val serverUpdatedAt: Long? = null,
    val syncError: String? = null
)
