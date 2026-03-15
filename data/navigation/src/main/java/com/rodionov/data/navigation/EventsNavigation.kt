package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Навигация для модуля событий.
 */
@Serializable
sealed class EventsNavigation: BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    /**
     * Базовый роут событий.
     */
    @Serializable
    data object EventsBaseRoute : EventsNavigation()

    /**
     * Роут списка событий.
     */
    @Serializable
    data object EventsRoute : EventsNavigation()

    /**
     * Роут деталей события.
     * @param eventId Идентификатор события.
     */
    @Serializable
    data class EventDetailsRoute(val eventId: Long) : EventsNavigation()

    /**
     * Роут группы участников события.
     * @param eventId Идентификатор события.
     * @param participantGroup Группа участников.
     */
    @Serializable
    data class EventParticipantGroupRoute(
        val eventId: Long,
        val participantGroup: EventParticipantGroup
    ) : EventsNavigation()
}
