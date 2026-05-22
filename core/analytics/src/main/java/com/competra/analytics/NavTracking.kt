package com.competra.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

/**
 * Подписывается на смену destination в [navController] и шлёт
 * `screen_view` через [tracker] для тех маршрутов, что замаплены
 * в [AnalyticsScreen.fromRoute].
 *
 * Маршруты без маппинга молча игнорируются — это намеренно,
 * чтобы технические destination'ы не засоряли отчёт.
 */
@Composable
fun TrackNavScreens(
    navController: NavController,
    tracker: AnalyticsTracker,
) {
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            AnalyticsScreen.fromRoute(entry.destination.route)?.let(tracker::trackScreen)
        }
    }
}
