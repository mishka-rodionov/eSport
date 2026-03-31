package com.rodionov.domain.repository.events

import com.rodionov.domain.models.Participant
import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant

/**
 * Репозиторий для получения деталей циклического события.
 */
interface CyclicEventDetailsRepository {

    /**
     * Получить детали события.
     * @param eventId Идентификатор события.
     */
    suspend fun getEventDetails(eventId: String): Result<CyclicEventDetails?>

    /**
     * Получить список участников группы события.
     * @param eventId Идентификатор события.
     * @param groupId Идентификатор группы.
     */
    suspend fun getParticipants(eventId: Long, groupId: Long): Result<List<OrienteeringParticipant>>

    /**
     * Зарегистрировать текущего пользователя в группу события.
     * @param eventId Идентификатор события.
     * @param groupId Идентификатор группы.
     */
    suspend fun registerToEvent(eventId: Long, groupId: Long): Result<Unit>

    /**
     * Отменить регистрацию текущего пользователя на событие.
     * @param eventId Идентификатор события.
     */
    suspend fun cancelRegistration(eventId: Long): Result<Unit>

}
