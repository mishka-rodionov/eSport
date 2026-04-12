package com.rodionov.remote.repository.events

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.events.EventStatus
import com.rodionov.domain.models.events.EventType
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.remote.datasource.events.CyclicEventDetailsRemoteDataSource
import com.rodionov.remote.request.events.RegisterEventRequest
import com.rodionov.remote.response.events.ParticipantPublicResponse

/**
 * Реализация репозитория для получения деталей соревнования.
 *
 * @param dataSource Источник данных для работы с API.
 */
class CyclicEventDetailsRepositoryImpl(
    private val dataSource: CyclicEventDetailsRemoteDataSource
) : CyclicEventDetailsRepository {

    override suspend fun getEventDetails(eventId: Long, userId: String?): Result<CyclicEventDetails?> {
        return dataSource.getEventDetails(eventId, userId)
            .map { response ->
                response.result?.let { dto ->
                    CyclicEventDetails(
                        eventId = dto.remoteId,
                        organizationId = dto.mainOrganizerId ?: "",
                        title = dto.title,
                        description = dto.description ?: "",
                        startDate = dto.startDate,
                        endDate = dto.endDate ?: dto.startDate,
                        endRegistrationDate = dto.registrationEnd ?: dto.startDate,
                        maxParticipants = dto.maxParticipants ?: 0,
                        city = dto.address ?: "",
                        participantGroups = dto.participantGroups.map { group ->
                            EventParticipantGroup(
                                groupId = group.groupId,
                                title = group.title,
                                description = null,
                                maxParticipant = group.maxParticipants ?: 0,
                                registeredParticipant = group.registeredCount
                            )
                        },
                        status = mapStatus(dto.status),
                        eventType = EventType.CyclicEvent.Orienteering,
                        isUserRegistered = dto.isUserRegistered
                    )
                }
            }
    }

    override suspend fun registerToEvent(
        eventId: Long,
        groupId: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        return dataSource.registerToEvent(
            RegisterEventRequest(
                competitionId = eventId,
                groupId = groupId,
                firstName = firstName,
                lastName = lastName
            )
        ).mapCatching { }
    }

    override suspend fun cancelRegistration(eventId: Long): Result<Unit> {
        return dataSource.cancelRegistration(eventId)
            .mapCatching { }
    }

    override suspend fun getParticipants(
        eventId: Long,
        groupId: String
    ): Result<List<OrienteeringParticipant>> {
        return dataSource.getParticipantsByGroup(groupId)
            .map { response ->
                response.result?.map { it.toDomain() } ?: emptyList()
            }
    }

    private fun ParticipantPublicResponse.toDomain(): OrienteeringParticipant {
        return OrienteeringParticipant(
            id = 0L,
            userId = userId ?: "",
            firstName = firstName,
            lastName = lastName,
            groupId = 0L,
            groupName = groupName,
            competitionId = 0L,
            commandName = commandName ?: "",
            startNumber = startNumber.toString(),
            startTime = startTime,
            chipNumber = chipNumber.toString(),
            comment = comment ?: "",
            isChipGiven = isChipGiven
        )
    }

    private fun mapStatus(status: String): EventStatus = when (status) {
        "REGISTRATION_OPEN" -> EventStatus.REGISTRATION
        "IN_PROGRESS" -> EventStatus.STARTED
        "FINISHED" -> EventStatus.FINISHED
        "CANCELLED" -> EventStatus.CANCELLED
        else -> EventStatus.CREATED
    }
}
