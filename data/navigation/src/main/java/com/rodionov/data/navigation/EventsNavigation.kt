package com.rodionov.data.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class EventsNavigation: BaseNavigation {
    @Serializable
    data object EventsBaseRoute : EventsNavigation()
    @Serializable
    data object EventsRoute : EventsNavigation()
    @Serializable
    data object EventDetailsRoute : EventsNavigation()
}