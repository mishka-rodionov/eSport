package com.competra.remote.repository.events

import com.competra.domain.models.Competition
import com.competra.domain.models.KindOfSport
import com.competra.domain.repository.events.EventsRepository
import com.competra.remote.datasource.events.EventsRemoteDataSource
import com.competra.remote.response.mappers.toDomain

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
