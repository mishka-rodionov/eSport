package com.rodionov.local.entities.orienteering


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.SplitTime
import com.rodionov.local.converters.ResultConverters

@Entity(
    tableName = "orienteering_results",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrienteeringParticipantEntity::class,
            parentColumns = ["id"],
            childColumns = ["participantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["competitionId"]),
        Index(value = ["participantId"])
    ]
)
@TypeConverters(ResultConverters::class)
data class OrienteeringResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val competitionId: Long,
    val participantId: Long,
    val startTime: Long? = null,
    val finishTime: Long? = null,
    val totalTime: Long? = null,
    val rank: Int? = null,
    val status: ResultStatus,
    val penaltyTime: Long = 0,
    val splits: List<SplitTime>? = null
)