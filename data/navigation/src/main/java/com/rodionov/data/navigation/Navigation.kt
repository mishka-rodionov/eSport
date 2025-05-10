package com.rodionov.data.navigation

import kotlinx.coroutines.flow.SharedFlow

sealed interface Navigation {

    val centerNavigationEffect: SharedFlow<CenterNavigation>
    val profileNavigationEffect: SharedFlow<ProfileNavigation>
    val eventsNavigationEffect: SharedFlow<EventsNavigation>

    suspend fun collectNavigationEffect(handler: (BaseNavigation) -> Unit, destination: BaseNavigation)

    suspend fun navigate(destination: BaseNavigation)

}