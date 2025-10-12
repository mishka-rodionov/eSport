package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Промежуточный класс данных (POJO) для представления связи "один ко многим"
 * между [ParticipantGroupEntity] и списком [OrienteeringParticipantEntity].
 *
 * Этот класс используется в Room для объединения группы участников с ее участниками.
 *
 * @property group Сущность группы участников.
 * @property participants Список участников, принадлежащих этой группе.
 */
data class ParticipantGroupWithParticipants(
    @Embedded
    val group: ParticipantGroupEntity,
    @Relation(
        parentColumn = "groupId", // Первичный ключ в ParticipantGroupEntity
        entityColumn = "groupId"  // Внешний ключ в ParticipantEntity
    )
    val participants: List<OrienteeringParticipantEntity>
)
