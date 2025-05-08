package com.rodionov.center.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.center.presentation.main.CenterScreen
import com.rodionov.center.presentation.kind_of_sport.KindOfSportScreen
import com.rodionov.data.navigation.BaseNavigation
import kotlinx.serialization.Serializable

@Serializable
sealed class CenterNavigationGraph: BaseNavigation {

    @Serializable
    data object CenterBaseRoute: CenterNavigationGraph()
    @Serializable
    data object CenterRoute: CenterNavigationGraph()
    @Serializable
    data object KindOfSportRoute: CenterNavigationGraph()

}

fun NavGraphBuilder.centerGraph() {
    navigation<CenterNavigationGraph.CenterBaseRoute>(startDestination = CenterNavigationGraph.CenterRoute) {
        composable<CenterNavigationGraph.CenterRoute> { CenterScreen() }
        composable<CenterNavigationGraph.KindOfSportRoute> { KindOfSportScreen() }
    }
}