package com.rodionov.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavigatorProvider
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.profile.presentation.about_app.AboutAppScreen
import com.rodionov.profile.presentation.main_profile.ProfileScreen
import com.rodionov.profile.presentation.profile_editor.ProfileEditorScreen
import kotlinx.serialization.Serializable

class ProfileNavigation(private val navigationProvider: NavigatorProvider) :
    Navigation.MainNavigation.Profile {
    override fun createNestedGraph(startDestination: String) {
        NavGraphBuilder(navigationProvider, screenName, "Profile").navigation(
            startDestination = startDestination,
            route = "main",
            builder = NavGraphBuilder::profileNavigation
        )
    }

}

@Serializable
sealed class ProfileNavigationGraph {
    @Serializable
    data object ProfileBaseRoute : ProfileNavigationGraph()
    @Serializable
    data object MainProfileRoute : ProfileNavigationGraph()
    @Serializable
    data object ProfileEditorRoute : ProfileNavigationGraph()
    @Serializable
    data object AboutAppRoute : ProfileNavigationGraph()
}

fun NavGraphBuilder.profileNavigation() {
    navigation<ProfileNavigationGraph.ProfileBaseRoute>(startDestination = ProfileNavigationGraph.MainProfileRoute) {
        composable<ProfileNavigationGraph.MainProfileRoute> { ProfileScreen() }
        composable<ProfileNavigationGraph.ProfileEditorRoute> { ProfileEditorScreen() }
        composable<ProfileNavigationGraph.AboutAppRoute> { AboutAppScreen() }
    }
}