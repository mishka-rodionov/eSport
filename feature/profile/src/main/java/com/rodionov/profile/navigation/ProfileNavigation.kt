package com.rodionov.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavigatorProvider
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.profile.presentation.about_app.AboutAppScreen
import com.rodionov.profile.presentation.main_profile.ProfileScreen
import com.rodionov.profile.presentation.profile_editor.ProfileEditorScreen

class ProfileNavigation(private val navigationProvider: NavigatorProvider) :
    Navigation.MainNavigation.Profile {
    override fun createNestedGraph(startDestination: String) {
        NavGraphBuilder(navigationProvider, screenName, "Profile").navigation(
            startDestination = startDestination,
            route = "main",
            builder = NavGraphBuilder::navigationBuild
        )
    }

}

sealed class ProfileNavigationGraph(val route: String) {
    data object MainProfile: ProfileNavigationGraph(route = "MainProfile")
    data object ProfileEditor: ProfileNavigationGraph(route = "ProfileEditor")
    data object AboutApp: ProfileNavigationGraph(route = "AboutApp")
}

fun NavGraphBuilder.navigationBuild() {
    composable(ProfileNavigationGraph.MainProfile.route) { ProfileScreen() }
    composable(ProfileNavigationGraph.ProfileEditor.route) { ProfileEditorScreen() }
    composable(ProfileNavigationGraph.AboutApp.route) { AboutAppScreen() }
}