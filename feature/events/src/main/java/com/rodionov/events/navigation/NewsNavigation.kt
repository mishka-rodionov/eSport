package com.rodionov.events.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.eventdetails.navigation.eventDetailsGraph
import com.rodionov.events.presentation.main.EventsScreen

/**
 * Граф навигации для модуля событий. Список событий + переиспользуемый подграф деталей.
 */
fun NavGraphBuilder.eventsGraph() {
    composable<EventsNavigation.EventsRoute> { EventsScreen() }
    eventDetailsGraph()
}
