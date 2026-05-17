package com.competra.events.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.competra.data.navigation.EventsNavigation
import com.competra.eventdetails.navigation.eventDetailsGraph
import com.competra.events.presentation.main.EventsScreen

/**
 * Граф навигации для модуля событий. Список событий + переиспользуемый подграф деталей.
 */
fun NavGraphBuilder.eventsGraph() {
    composable<EventsNavigation.EventsRoute> { EventsScreen() }
    eventDetailsGraph()
}
