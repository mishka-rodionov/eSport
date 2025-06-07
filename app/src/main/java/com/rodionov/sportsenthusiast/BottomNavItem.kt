package com.rodionov.sportsenthusiast

import kotlinx.serialization.Serializable

@Serializable
sealed class BottomNavItem(val route: String, val title: String) {
    @Serializable
    data object Profile : BottomNavItem(route = "Profile", title = "Profile")
    @Serializable
    data object CompetitionConstructor :
        BottomNavItem(route = "CompetitionConstructor", title = "Center")
    @Serializable
    data object CompetitionList :
        BottomNavItem(route = "CompetitionList", title = "News")
}