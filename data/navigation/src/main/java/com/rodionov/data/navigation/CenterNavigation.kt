package com.rodionov.data.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class CenterNavigation: BaseNavigation {

    @Serializable
    data object CenterBaseRoute: CenterNavigation()
    @Serializable
    data object CenterRoute: CenterNavigation()
    @Serializable
    data object KindOfSportRoute: CenterNavigation()

}