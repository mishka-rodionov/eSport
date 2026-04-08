package com.rodionov.events.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rodionov.data.navigation.EventParticipantGroupNavType
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.events.presentation.event_results.EventResultsScreen
import com.rodionov.events.presentation.main.EventsScreen
import com.rodionov.events.presentation.eventDetails.EventDetailsScreen
import com.rodionov.events.presentation.event_participant_group.EventParticipantGroupScreen
import kotlin.reflect.typeOf

/**
 * Граф навигации для модуля событий.
 */
fun NavGraphBuilder.eventsGraph() {
//    navigation<EventsNavigationGraph.EventsBaseRoute>(startDestination = EventsNavigationGraph.EventsRoute) {
        composable<EventsNavigation.EventsRoute> { EventsScreen() }
        composable<EventsNavigation.EventDetailsRoute> {
            val route = it.toRoute<EventsNavigation.EventDetailsRoute>()
            EventDetailsScreen(idEvent = route.eventId)
        }
        composable<EventsNavigation.EventParticipantGroupRoute>(
            typeMap = mapOf(typeOf<EventParticipantGroup>() to EventParticipantGroupNavType)
        ) {
            val route = it.toRoute<EventsNavigation.EventParticipantGroupRoute>()
            EventParticipantGroupScreen(
                eventId = route.eventId,
                participantGroup = route.participantGroup
            )
        }
        composable<EventsNavigation.EventResultsRoute> {
            val route = it.toRoute<EventsNavigation.EventResultsRoute>()
            EventResultsScreen(eventId = route.eventId)
        }
//    }
}
