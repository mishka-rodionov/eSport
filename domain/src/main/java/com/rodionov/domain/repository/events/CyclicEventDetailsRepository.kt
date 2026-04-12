package com.rodionov.domain.repository.events

import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.orienteering.OrienteeringParticipant

/**
 * Репозиторий для получения деталей циклического события.
 */
interface CyclicEventDetailsRepository {

    /**
     * Получить детали события.
     * @param eventId Идентификатор события.
     * @param userId ID текущего пользователя для проверки регистрации (null = не проверять).
     */
    suspend fun getEventDetails(eventId: Long, userId: String? = null): Result<CyclicEventDetails?>

    /**
     * Получить список участников группы события.
     * @param eventId Идентификатор события.
     * @param groupId Идентификатор группы.
     */
    suspend fun getParticipants(eventId: Long, groupId: String): Result<List<OrienteeringParticipant>>

    /**
     * Зарегистрировать текущего пользователя в группу события.
     * @param eventId Идентификатор события (competitionId на сервере).
     * @param groupId Идентификатор группы.
     * @param firstName Имя пользователя.
     * @param lastName Фамилия пользователя.
     */
    suspend fun registerToEvent(eventId: Long, groupId: String, firstName: String, lastName: String): Result<Unit>

    /**
     * Отменить регистрацию текущего пользователя на событие.
     * @param eventId Идентификатор события.
     */
    suspend fun cancelRegistration(eventId: Long): Result<Unit>

}
