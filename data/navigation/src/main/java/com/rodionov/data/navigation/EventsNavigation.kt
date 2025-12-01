package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class EventsNavigation: BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    @Serializable
    data object EventsBaseRoute : EventsNavigation()
    @Serializable
    data object EventsRoute : EventsNavigation()
    @Serializable
    data object EventDetailsRoute : EventsNavigation()
}