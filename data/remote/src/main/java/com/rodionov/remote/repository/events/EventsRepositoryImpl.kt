package com.rodionov.remote.repository.events

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.remote.datasource.events.EventsRemoteDataSource
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse
import com.rodionov.remote.response.mappers.toDomain

class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource
) : EventsRepository {

    override suspend fun getEvents(kindOfSport: List<KindOfSport>): Result<List<Competition>?> {
//        return eventsRemoteDataSource.getEvents(kindOfSport = kindOfSport.map { it.name })
//            .map { response -> response.result?.map { it.toDomain() } }
        return Result.success(mockEvents().map { it.toDomain() })
    }

    fun mockEvents(): List<CompetitionResponse> {
        return listOf(
            CompetitionResponse(
                title = "Городские соревнования",
                date = 1756018800,
                kindOfSport = "Orienteering",
                description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
                address = "Саратов",
                coordinates = CoordinatesResponse(
                    latitude = 51.3200,
                    longitude = 46.0000
                )
            ),
            CompetitionResponse(
                title = "Городские соревнования #2",
                date = 	1756278000,
                kindOfSport = "Orienteering",
                description = "Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию. Новые соревнования по ориентированию.",
                address = "Балашов",
                coordinates = CoordinatesResponse(
                    latitude = 51.3200,
                    longitude = 46.0000
                )
            )
        )
    }
}