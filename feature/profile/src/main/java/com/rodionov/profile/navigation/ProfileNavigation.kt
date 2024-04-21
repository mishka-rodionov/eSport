package com.rodionov.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavigatorProvider
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.profile.presentation.ProfileScreen

sealed class ProfileNavigation(private val navigationProvider: NavigatorProvider) :
    Navigation.MainNavigation.Profile {
    override fun createNestedGraph() {
        NavGraphBuilder(navigationProvider, screenName, "").navigation(
            startDestination = "",
            route = "",
            builder = NavGraphBuilder::navigationBuild
        )
    }

}

fun NavGraphBuilder.navigationBuild() {
    composable("") { ProfileScreen() }
}