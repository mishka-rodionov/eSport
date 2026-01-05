package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orienteering_participants",
    foreignKeys = [
        ForeignKey(
            entity = ParticipantGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE // Участники удаляются при удалении группы
        )
    ],
//    indices = [
//        Index(value = ["competitionId", "startNumber"], unique = true),
//        Index(value = ["chipNumber"], unique = true),
//        Index(value = ["competitionId", "groupId"])
//    ]
)
data class OrienteeringParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val groupId: Long, // Поле для связи с ParticipantGroupEntity
    val competitionId: Long,
    val commandName: String,
    val startNumber: String,
    val startTime: String,
    val chipNumber: String,
    val comment: String
)
