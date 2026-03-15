package com.rodionov.remote.repository.events

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository

class CyclicEventDetailsRepositoryImpl: CyclicEventDetailsRepository {

    override suspend fun getEventDetails(eventId: String): Result<CyclicEventDetails?> {
        return Result.success(
            CyclicEventDetails(
                eventId = 123L,
                organizationId = "1234",
                title = "Соревнования по ориентированию",
                description = "Традиционный старт",
                startDate = 123,
                endDate = 223,
                endRegistrationDate = 220,
                maxParticipants = 100,
                city = "Саратов",
                participantGroups = listOf(
                    EventParticipantGroup(
                        groupId = 12345,
                        title = "М21",
                        description = "Основная группа",
                        maxParticipant = 50,
                        registeredParticipant = 21
                    )
                )
            )
        )
    }
}