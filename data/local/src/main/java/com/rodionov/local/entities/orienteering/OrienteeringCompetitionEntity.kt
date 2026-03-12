package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.local.converters.CompetitionConverters

/**
 * Сущность соревнования по ориентированию для базы данных Room.
 * 
 * @property id Уникальный идентификатор
 * @property competition Базовая информация о соревновании
 * @property direction Направление
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим начала соревнования
 */
@Entity(tableName = "orienteering_competitions")
@TypeConverters(CompetitionConverters::class)
data class OrienteeringCompetitionEntity(
    @PrimaryKey
    val id: Long = 0,
    @Embedded
    val competition: Competition,
    val direction: OrienteeringDirection,
    val punchingSystem: PunchingSystem,
    val startTimeMode: StartTimeMode
)
