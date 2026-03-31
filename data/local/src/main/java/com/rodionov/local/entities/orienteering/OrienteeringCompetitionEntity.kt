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
 * @property localCompetitionId Уникальный идентификатор
 * @property competition Базовая информация о соревновании
 * @property direction Направление
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим начала соревнования
 * @property countdownTimer Время отсчета перед стартом (в минутах)
 * @property startTime Фактическое время начала соревнования (timestamp)
 */
@Entity(tableName = "orienteering_competitions")
@TypeConverters(CompetitionConverters::class)
data class OrienteeringCompetitionEntity(
    @PrimaryKey(autoGenerate = true)
    val localCompetitionId: Long = 0,
    @Embedded
    val competition: Competition,
    val direction: OrienteeringDirection,
    val punchingSystem: PunchingSystem,
    val startTimeMode: StartTimeMode,
    val countdownTimer: Int? = null,
    val startTime: Long? = null
)
