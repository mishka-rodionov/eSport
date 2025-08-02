package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.local.converters.CompetitionConverters

@Entity(tableName = "orienteering_competitions")
@TypeConverters(CompetitionConverters::class)
data class OrienteeringCompetitionEntity(
    @PrimaryKey
    val id: Long = 0,
    @Embedded
    val competition: Competition,
    val direction: OrienteeringDirection
)