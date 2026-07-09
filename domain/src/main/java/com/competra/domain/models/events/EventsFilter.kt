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
 * @property includeTest Включать ли тестовые соревнования в выдачу (debug-предпросмотр; по умолчанию они
 * скрыты из публичной ленты на сервере). Выставляется только из debug-сборки.
 * @property searchQuery Текстовый поиск по названию соревнования (contains, регистронезависимо). null — без ограничения.
 */
data class EventsFilter(
    val kindOfSports: List<KindOfSport> = emptyList(),
    val statuses: List<CompetitionStatus> = emptyList(),
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val includeTest: Boolean = false,
    val searchQuery: String? = null
) {
    val isEmpty: Boolean
        get() = kindOfSports.isEmpty() &&
                statuses.isEmpty() &&
                dateFrom == null &&
                dateTo == null &&
                !includeTest &&
                searchQuery.isNullOrBlank()
}
