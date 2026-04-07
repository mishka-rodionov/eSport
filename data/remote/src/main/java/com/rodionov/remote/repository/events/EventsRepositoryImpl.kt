package com.rodionov.remote.repository.events

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.remote.datasource.events.EventsRemoteDataSource
import com.rodionov.remote.response.mappers.toDomain

/**
 * Реализация репозитория для работы со списком событий.
 */
class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource
) : EventsRepository {

    override suspend fun getEvents(kindOfSport: List<KindOfSport>): Result<List<Competition>?> {
        return eventsRemoteDataSource.getEvents(kindOfSport = kindOfSport.map { it.name })
            .map { response -> response.result?.map { it.toDomain() } }
    }
}
