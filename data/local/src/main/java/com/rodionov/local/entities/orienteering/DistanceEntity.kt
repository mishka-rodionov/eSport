package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.local.converters.ControlPointConverters

@Entity(
    tableName = "distances",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("competitionId")]
)
@TypeConverters(ControlPointConverters::class)
data class DistanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: Long? = null,
    val competitionId: Long,
    val name: String? = null,               // например, "Длинная дистанция"
    val lengthMeters: Int,                  // длина в метрах
    val climbMeters: Int,                   // набор высоты
    val controlsCount: Int,                 // количество КП
    val description: String? = null,
    // Поля синхронизации
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val controlPoints: List<ControlPoint>
)
