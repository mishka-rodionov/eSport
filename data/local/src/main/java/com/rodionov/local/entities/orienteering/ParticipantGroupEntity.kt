package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participant_groups")
data class ParticipantGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val competitionId: Long,
    val title: String,
    val distance: Double,
    val countOfControls: Int,
    val maxTimeInMinute: Int
)

