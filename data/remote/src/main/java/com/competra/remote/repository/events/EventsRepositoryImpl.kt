package com.competra.remote.repository.events

import com.competra.domain.models.Competition
import com.competra.domain.models.events.EventsFilter
import com.competra.domain.repository.events.EventsRepository
import com.competra.remote.datasource.events.EventsRemoteDataSource
import com.competra.remote.response.mappers.toDomain

/**
 * Реализация репозитория для работы со списком событий.
 */
class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource
) : EventsRepository {

    override suspend fun getEvents(filter: EventsFilter): Result<List<Competition>?> {
        return eventsRemoteDataSource.getEvents(
            kindOfSports = filter.kindOfSports.map { it.name },
            statuses = filter.statuses.map { it.name },
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            includeTest = filter.includeTest
        ).map { response -> response.result?.map { it.toDomain() } }
    }
}
