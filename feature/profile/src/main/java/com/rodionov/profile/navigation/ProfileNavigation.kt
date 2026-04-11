package com.rodionov.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.profile.presentation.about_app.AboutAppScreen
import com.rodionov.profile.presentation.auth.AuthScreen
import com.rodionov.profile.presentation.auth_code.AuthCodeScreen
import com.rodionov.profile.presentation.main_profile.ProfileScreen
import com.rodionov.profile.presentation.profile_editor.ProfileEditorScreen
import com.rodionov.profile.presentation.registration.RegistrationScreen

fun NavGraphBuilder.profileNavigation() {
//    navigation<ProfileNavigationGraph.ProfileBaseRoute>(startDestination = ProfileNavigationGraph.MainProfileRoute) {
        composable<ProfileNavigation.MainProfileRoute> { ProfileScreen() }
        composable<ProfileNavigation.ProfileEditorRoute> { ProfileEditorScreen() }
        composable<ProfileNavigation.AboutAppRoute> { AboutAppScreen() }
        composable<ProfileNavigation.AuthRoute> { AuthScreen() }
        composable<ProfileNavigation.AuthCodeRoute> {
                val route = it.toRoute<ProfileNavigation.AuthCodeRoute>()
                AuthCodeScreen(userEmail = route.email) }
        composable<ProfileNavigation.RegistrationRoute> { RegistrationScreen() }
//    }
}