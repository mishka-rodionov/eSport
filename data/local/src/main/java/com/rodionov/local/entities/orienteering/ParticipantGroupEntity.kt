package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.converters.ControlPointConverters

@Entity(
    tableName = "participant_groups",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(ControlPointConverters::class)
data class ParticipantGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val competitionId: Long,
    val title: String,
    val distance: Double,
    val countOfControls: Int,
    val maxTimeInMinute: Int,
    val controlPoints: List<ControlPoint>
)

