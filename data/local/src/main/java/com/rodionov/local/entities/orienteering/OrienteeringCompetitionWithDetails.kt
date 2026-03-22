/**
 * POJO (Plain Old Java Object) для представления соревнования по спортивному ориентированию
 * со всеми его деталями, включая группы участников и самих участников.
 *
 * Этот класс используется Room для объединения данных из нескольких таблиц:
 * - `OrienteeringCompetitionEntity` (основная информация о соревновании)
 * - `ParticipantGroupEntity` (группы участников в соревновании)
 * - `OrienteeringParticipantEntity` (участники в каждой группе)
 *
 * @property competition Основная сущность соревнования.
 * @property groupsWithParticipants Список групп участников, каждая из которых содержит свой список участников.
 *                                  Room автоматически выполняет вложенные запросы для заполнения этой связи.
 */
package com.rodionov.local.entities.orienteering

import androidx.room.Embedded
import androidx.room.Relation

data class OrienteeringCompetitionWithDetails(
    @Embedded
    val competition: OrienteeringCompetitionEntity,

    @Relation(
        entity = ParticipantGroupEntity::class, // Указываем промежуточную сущность
        parentColumn = "localCompetitionId", // Поле из OrienteeringCompetitionEntity
        entityColumn = "competitionId" // Поле из ParticipantGroupEntity
    )
    val groupsWithParticipants: List<ParticipantGroupWithParticipants>
)