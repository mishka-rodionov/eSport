package com.rodionov.data.navigation

sealed interface Navigation {

    val screenName: String

    fun createNestedGraph() = run { }

    sealed interface MainNavigation: Navigation {
        interface Profile: MainNavigation {
            override val screenName: String
                get() = "Profile"
        }

        interface CompetitionConstructor: MainNavigation {
            override val screenName: String
                get() = "CompetitionConstructor"
        }

        interface CompetitionList: MainNavigation {
            override val screenName: String
                get() = "CompetitionList"
        }
    }
}