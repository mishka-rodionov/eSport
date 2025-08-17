package com.rodionov.events.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.events.presentation.main.EventsScreen
import com.rodionov.events.presentation.eventDetails.EventDetailsScreen

fun NavGraphBuilder.eventsGraph() {
//    navigation<EventsNavigationGraph.EventsBaseRoute>(startDestination = EventsNavigationGraph.EventsRoute) {
        composable<EventsNavigation.EventsRoute> { EventsScreen() }
        composable<EventsNavigation.EventDetailsRoute> { EventDetailsScreen() }
//    }
}