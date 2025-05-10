package com.rodionov.news.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.news.presentation.NewsScreen
import com.rodionov.news.presentation.eventDetails.EventDetailsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.eventsGraph() {
//    navigation<EventsNavigationGraph.EventsBaseRoute>(startDestination = EventsNavigationGraph.EventsRoute) {
        composable<EventsNavigation.EventsRoute> { NewsScreen() }
        composable<EventsNavigation.EventDetailsRoute> { EventDetailsScreen() }
//    }
}