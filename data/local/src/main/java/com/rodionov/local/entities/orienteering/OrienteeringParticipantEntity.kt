package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Сущность участника соревнований по ориентированию для базы данных Room.
 * 
 * @property isChipGiven Флаг выдачи чипа.
 */
@Entity(
    tableName = "orienteering_participants",
    foreignKeys = [
        ForeignKey(
            entity = ParticipantGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class OrienteeringParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val groupId: Long,
    val groupName : String,
    val competitionId: Long,
    val commandName: String,
    val startNumber: String,
    val startTime: Long,
    val chipNumber: String,
    val comment: String,
    val isChipGiven: Boolean,
    val isSynced: Boolean = false
)
