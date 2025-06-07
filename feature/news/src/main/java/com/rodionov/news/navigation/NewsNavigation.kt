package com.rodionov.news.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.news.presentation.NewsScreen
import com.rodionov.news.presentation.eventDetails.EventDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class EventsNavigationGraph {
    @Serializable
    data object EventsBaseRoute : EventsNavigationGraph()
    @Serializable
    data object EventsRoute : EventsNavigationGraph()
    @Serializable
    data object EventDetailsRoute : EventsNavigationGraph()
}

fun NavGraphBuilder.eventsGraph() {
    navigation<EventsNavigationGraph.EventsBaseRoute>(startDestination = EventsNavigationGraph.EventsRoute) {
        composable<EventsNavigationGraph.EventsRoute> { NewsScreen() }
        composable<EventsNavigationGraph.EventDetailsRoute> { EventDetailsScreen() }
    }
}