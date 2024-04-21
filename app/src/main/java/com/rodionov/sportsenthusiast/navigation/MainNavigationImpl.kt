package com.rodionov.sportsenthusiast.navigation

import com.rodionov.data.navigation.Navigation

sealed class MainNavigationImpl (override val screenName: String): Navigation.MainNavigation {
    interface Profile: Navigation.MainNavigation {
        override val screenName: String
            get() = "Profile"
    }
    class CompetitionConstructor(): MainNavigationImpl(screenName = "CompetitionConstructor")
    class CompetitionList: MainNavigationImpl(screenName = "CompetitionList")
}