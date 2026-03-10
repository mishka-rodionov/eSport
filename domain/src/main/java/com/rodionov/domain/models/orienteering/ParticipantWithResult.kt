package com.rodionov.domain.models.orienteering

/**
 * Класс данных, представляющий участника соревнований по ориентированию вместе с его результатом.
 *
 * @property participant Данные участника соревнований.
 * @property result Результат участника (может быть null, если результат еще не определен или отсутствует).
 */
data class ParticipantWithResult(
    val participant: OrienteeringParticipant,
    val result: OrienteeringResult?
)
