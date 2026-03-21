package com.rodionov.remote.repository.events

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.remote.datasource.events.EventsRemoteDataSource
import com.rodionov.remote.response.competition.CompetitionResponse
import com.rodionov.remote.response.competition.CoordinatesResponse
import com.rodionov.remote.response.mappers.toDomain

/**
 * Реализация репозитория для работы со списком событий.
 * На данный момент использует моковые данные для демонстрации.
 */
class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource
) : EventsRepository {

    override suspend fun getEvents(kindOfSport: List<KindOfSport>): Result<List<Competition>?> {
//        return eventsRemoteDataSource.getEvents(kindOfSport = kindOfSport.map { it.name })
//            .map { response -> response.result?.map { it.toDomain() } }
        return Result.success(mockEvents().map { it.toDomain() })
    }

    /**
     * Возвращает список моковых ответов сервера для новой модели CompetitionResponse.
     */
    fun mockEvents(): List<CompetitionResponse> {
        return listOf(
            CompetitionResponse(
                remoteId = 1L,
                title = "Городские соревнования по ориентированию",
                startDate = 1756018800000L, // timestamp в мс
                endDate = 1756105200000L,
                kindOfSport = "Orienteering",
                description = "Традиционные городские соревнования для всех категорий участников. Ждем вас в городском парке!",
                address = "Саратов, Городской парк",
                mainOrganizerId = 123L,
                coordinates = CoordinatesResponse(
                    latitude = 51.5335,
                    longitude = 46.0342
                ),
                status = "REGISTRATION",
                registrationStart = 1754018800000L,
                registrationEnd = 1755918800000L,
                maxParticipants = 300,
                feeAmount = 500.0,
                feeCurrency = "RUB",
                regulationUrl = "http://example.com/regulation1.pdf",
                mapUrl = "http://example.com/map1.jpg",
                contactPhone = "+78452000000",
                contactEmail = "org@saratov-orient.ru",
                website = "http://saratov-orient.ru",
                resultsStatus = "NOT_PUBLISHED"
            ),
            CompetitionResponse(
                remoteId = 2L,
                title = "Кубок Левобережья",
                startDate = 1756278000000L,
                endDate = 1756364400000L,
                kindOfSport = "Orienteering",
                description = "Масштабные соревнования на открытой местности. Сложные дистанции и ценные призы.",
                address = "Энгельс, лесной массив",
                mainOrganizerId = 124L,
                coordinates = CoordinatesResponse(
                    latitude = 51.4800,
                    longitude = 46.1200
                ),
                status = "DRAFT",
                registrationStart = 1755018800000L,
                registrationEnd = 1756178000000L,
                maxParticipants = 500,
                feeAmount = 350.0,
                feeCurrency = "RUB",
                regulationUrl = "http://example.com/regulation2.pdf",
                mapUrl = null,
                contactPhone = "+78453000000",
                contactEmail = "info@cup.ru",
                website = null,
                resultsStatus = "NOT_PUBLISHED"
            )
        )
    }
}
