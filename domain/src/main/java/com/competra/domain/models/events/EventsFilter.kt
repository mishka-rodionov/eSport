package com.competra.domain.models.events

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.orienteering.CompetitionStatus

/**
 * Фильтр списка публичных соревнований.
 *
 * @property kindOfSports Выбранные виды спорта. Пустой список — без ограничения.
 * @property statuses Выбранные статусы (комбинируются через OR). Пустой список — без ограничения.
 * @property dateFrom Начало диапазона дат старта соревнования, millis, включительно. null — без ограничения.
 * @property dateTo Конец диапазона дат старта соревнования, millis, включительно. null — без ограничения.
 */
data class EventsFilter(
    val kindOfSports: List<KindOfSport> = emptyList(),
    val statuses: List<CompetitionStatus> = emptyList(),
    val dateFrom: Long? = null,
    val dateTo: Long? = null
) {
    val isEmpty: Boolean
        get() = kindOfSports.isEmpty() &&
                statuses.isEmpty() &&
                dateFrom == null &&
                dateTo == null
}
