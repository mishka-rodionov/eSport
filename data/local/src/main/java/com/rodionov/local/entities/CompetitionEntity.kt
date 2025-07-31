package com.rodionov.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.local.converters.CompetitionConverters
import java.time.LocalDate

@Entity(tableName = "competitions")
@TypeConverters(CompetitionConverters::class)
data class CompetitionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val date: LocalDate,
    val kindOfSport: KindOfSport,
    val description: String,
    val address: String,
    val coordinates: Coordinates
)
