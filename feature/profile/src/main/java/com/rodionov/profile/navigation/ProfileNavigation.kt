package com.rodionov.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.profile.presentation.about_app.AboutAppScreen
import com.rodionov.profile.presentation.auth.AuthScreen
import com.rodionov.profile.presentation.auth_code.AuthCodeScreen
import com.rodionov.profile.presentation.main_profile.ProfileScreen
import com.rodionov.profile.presentation.profile_editor.ProfileEditorScreen

fun NavGraphBuilder.profileNavigation() {
//    navigation<ProfileNavigationGraph.ProfileBaseRoute>(startDestination = ProfileNavigationGraph.MainProfileRoute) {
        composable<ProfileNavigation.MainProfileRoute> { ProfileScreen() }
        composable<ProfileNavigation.ProfileEditorRoute> { ProfileEditorScreen() }
        composable<ProfileNavigation.AboutAppRoute> { AboutAppScreen() }
        composable<ProfileNavigation.AuthRoute> { AuthScreen() }
        composable<ProfileNavigation.AuthCodeRoute> { AuthCodeScreen() }
//    }
}