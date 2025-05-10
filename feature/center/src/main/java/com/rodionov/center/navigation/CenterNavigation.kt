package com.rodionov.center.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodionov.center.presentation.main.CenterScreen
import com.rodionov.center.presentation.kind_of_sport.KindOfSportScreen
import com.rodionov.data.navigation.CenterNavigation

fun NavGraphBuilder.centerGraph() {
//    navigation<CenterNavigationGraph.CenterBaseRoute>(startDestination = CenterNavigationGraph.CenterRoute) {
        composable<CenterNavigation.CenterRoute> { CenterScreen() }
        composable<CenterNavigation.KindOfSportRoute> { KindOfSportScreen() }
//    }
}