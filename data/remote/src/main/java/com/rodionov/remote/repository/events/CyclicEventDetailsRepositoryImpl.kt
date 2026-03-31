package com.rodionov.remote.repository.events

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.events.EventStatus
import com.rodionov.domain.models.events.EventType
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.repository.events.CyclicEventDetailsRepository
import com.rodionov.remote.datasource.events.CyclicEventDetailsRemoteDataSource
import com.rodionov.remote.request.events.RegisterEventRequest

/**
 * Реализация репозитория для получения деталей циклического события.
 * Содержит моковые данные для [getEventDetails] и [getParticipants].
 * Методы регистрации используют реальные сетевые запросы.
 *
 * @param dataSource Источник данных для работы с API деталей событий.
 */
class CyclicEventDetailsRepositoryImpl(
    private val dataSource: CyclicEventDetailsRemoteDataSource
) : CyclicEventDetailsRepository {

    override suspend fun getEventDetails(eventId: String): Result<CyclicEventDetails?> {
        return Result.success(
            CyclicEventDetails(
                eventId = 1L,
                organizationId = "org_1",
                title = "Марафон \"Путь к успеху\"",
                description = "Большой забег через весь город. Приглашаем всех желающих испытать свои силы и насладиться видами нашего прекрасного города.",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000L,
                endRegistrationDate = System.currentTimeMillis() - 3600000L,
                maxParticipants = 500,
                city = "Москва",
                participantGroups = listOf(
                    EventParticipantGroup(1, "М21", "Профессионалы", 100, 45),
                    EventParticipantGroup(2, "Ж21", "Профессионалы", 100, 30),
                    EventParticipantGroup(3, "Open", "Любители", 300, 150)
                ),
                status = EventStatus.REGISTRATION,
                eventType = EventType.CyclicEvent.Orienteering
            )
        )
    }

    override suspend fun registerToEvent(eventId: Long, groupId: Long): Result<Unit> {
        return dataSource.registerToEvent(RegisterEventRequest(eventId = eventId, groupId = groupId))
            .mapCatching { }
    }

    override suspend fun cancelRegistration(eventId: Long): Result<Unit> {
        return dataSource.cancelRegistration(eventId)
            .mapCatching { }
    }

    /**
     * Возвращает моковый список участников для заданной группы и события.
     * @param eventId Идентификатор события.
     * @param groupId Идентификатор группы.
     */
    override suspend fun getParticipants(
        eventId: Long,
        groupId: Long
    ): Result<List<OrienteeringParticipant>> {
        return Result.success(
            listOf(
                OrienteeringParticipant(
                    id = 101L,
                    userId = "user_1",
                    firstName = "Иван",
                    lastName = "Иванов",
                    groupId = groupId,
                    groupName = "М21",
                    competitionId = eventId,
                    commandName = "Спартак",
                    startNumber = "1",
                    startTime = System.currentTimeMillis(),
                    chipNumber = "CHIP001",
                    comment = "Мастер спорта",
                    isChipGiven = true
                ),
                OrienteeringParticipant(
                    id = 102L,
                    userId = "user_2",
                    firstName = "Петр",
                    lastName = "Петров",
                    groupId = groupId,
                    groupName = "М21",
                    competitionId = eventId,
                    commandName = "Динамо",
                    startNumber = "2",
                    startTime = System.currentTimeMillis() + 60000,
                    chipNumber = "CHIP002",
                    comment = "",
                    isChipGiven = false
                ),
                OrienteeringParticipant(
                    id = 103L,
                    userId = "user_3",
                    firstName = "Сидор",
                    lastName = "Сидоров",
                    groupId = groupId,
                    groupName = "М21",
                    competitionId = eventId,
                    commandName = "Зенит",
                    startNumber = "3",
                    startTime = System.currentTimeMillis() + 120000,
                    chipNumber = "CHIP003",
                    comment = "КМС",
                    isChipGiven = true
                )
            )
        )
    }
}
