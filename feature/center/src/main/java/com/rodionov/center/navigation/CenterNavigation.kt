package com.rodionov.center.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.rodionov.center.presentation.event_control.orienteering.OrienteeringEventControlScreen
import com.rodionov.center.presentation.main.CenterScreen
import com.rodionov.center.presentation.kind_of_sport.KindOfSportScreen
import com.rodionov.center.presentation.orientiring_competition_create.OrienteeringCompetitionCreator
import com.rodionov.data.navigation.CenterNavigation

fun NavGraphBuilder.centerGraph(windowSizeClass: WindowSizeClass) {
//    navigation<CenterNavigationGraph.CenterBaseRoute>(startDestination = CenterNavigationGraph.CenterRoute) {
    composable<CenterNavigation.CenterRoute> { CenterScreen() }
    composable<CenterNavigation.KindOfSportRoute> { KindOfSportScreen() }
    composable<CenterNavigation.OrienteeringCreatorRoute> { OrienteeringCompetitionCreator() }
    composable<CenterNavigation.OrienteeringEventControlRoute> {
        OrienteeringEventControlScreen(
            windowSizeClass = windowSizeClass
        )
    }
//    }
}