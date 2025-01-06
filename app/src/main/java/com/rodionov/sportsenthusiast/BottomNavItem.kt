package com.rodionov.sportsenthusiast

sealed class BottomNavItem(val route: String, val title: String) {
    data object Profile : BottomNavItem(route = "Profile", title = "Profile")
    data object CompetitionConstructor :
        BottomNavItem(route = "CompetitionConstructor", title = "Center")

    data object CompetitionList :
        BottomNavItem(route = "CompetitionList", title = "News")
}