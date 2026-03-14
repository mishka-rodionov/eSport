package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ResultStatus

/**
 * Модель результата участника в соревнованиях по ориентированию.
 *
 * @property id Уникальный идентификатор.
 * @property competitionId Идентификатор соревнования.
 * @property groupId Идентификатор группы.
 * @property participantId Идентификатор участника.
 * @property startTime Время старта (мс).
 * @property finishTime Время финиша (мс).
 * @property totalTime Общее время (в секундах).
 * @property rank Место.
 * @property status Статус результата (FINISHED, DSQ, DNS, DNF).
 * @property penaltyTime Штрафное время.
 * @property splits Сплиты (отметки на КП).
 * @property isEditable Флаг возможности редактирования результата.
 * @property isEdited Флаг того, что результат был изменен вручную.
 */
data class OrienteeringResult(
    val id: Long = 0,
    val competitionId: Long,
    val groupId: Long,
    val participantId: Long,
    val startTime: Long? = null,
    val finishTime: Long? = null,
    val totalTime: Long? = null, // в секундах
    val rank: Int? = null,
    val status: ResultStatus, // FINISHED, DSQ, DNS, DNF
    val penaltyTime: Long = 0, // Штрафное время
    val splits: List<SplitTime>? = null, // Можно хранить как JSON или отдельной таблицей
    val isEditable: Boolean = true,
    val isEdited: Boolean = false
)
