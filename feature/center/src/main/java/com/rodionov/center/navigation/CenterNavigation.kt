package com.rodionov.center.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.center.presentation.CenterScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class CenterNavigationGraph {

    @Serializable
    data object CenterBaseRoute: CenterNavigationGraph()
    @Serializable
    data object CenterRoute: CenterNavigationGraph()

}

fun NavGraphBuilder.centerGraph() {
    navigation<CenterNavigationGraph.CenterBaseRoute>(startDestination = CenterNavigationGraph.CenterRoute) {
        composable<CenterNavigationGraph.CenterRoute> { CenterScreen() }
    }
}