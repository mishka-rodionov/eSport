package com.rodionov.local.entities.orienteering

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.local.converters.CompetitionConverters
import com.rodionov.local.converters.ControlPointConverters
import com.rodionov.local.converters.UserConverter

/**
 * Entity (сущность) для представления группы участников в соревновании по спортивному ориентированию.
 * Группа определяет параметры дистанции для определенной категории участников
 * (например, "Мужчины элита", "Женщины 16-17 лет" и т.д.).
 *
 * @property groupId Уникальный идентификатор группы (автогенерируемый)
 * @property competitionId Идентификатор соревнования, к которому относится группа (внешний ключ)
 * @property title Название группы (например, "М21", "Ж16", "Open")
 * @property distance Длина дистанции в километрах
 * @property countOfControls Количество контрольных пунктов (КП) на дистанции
 * @property maxTimeInMinute Максимальное время прохождения дистанции в минутах (контрольное время)
 * @property controlPoints Список контрольных пунктов с их координатами и порядком прохождения
 */
@Entity(
    tableName = "participant_groups",
    foreignKeys = [
        ForeignKey(
            entity = OrienteeringCompetitionEntity::class,
            parentColumns = ["localCompetitionId"],
            childColumns = ["competitionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(ControlPointConverters::class, UserConverter::class)
data class ParticipantGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val competitionId: Long,
    val title: String,
    val gender: Gender?,                   // MALE, FEMALE, MIXED, null – не задано
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val distanceId: Long,                  // ссылка на дистанцию
    val maxParticipants: Int? = null,      // лимит для группы
    // Поля синхронизации
    val remoteId: String? = null,          // UUID группы на сервере
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

