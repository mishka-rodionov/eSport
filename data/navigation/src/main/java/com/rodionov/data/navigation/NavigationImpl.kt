package com.rodionov.data.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

class NavigationImpl: Navigation {

    private val _centerNavigationEffect = MutableSharedFlow<CenterNavigation>()
    override val centerNavigationEffect: SharedFlow<CenterNavigation> = _centerNavigationEffect.asSharedFlow()
    private val _profileNavigationEffect = MutableSharedFlow<ProfileNavigation>()
    override val profileNavigationEffect: SharedFlow<ProfileNavigation> = _profileNavigationEffect.asSharedFlow()
    private val _eventsNavigationEffect = MutableSharedFlow<EventsNavigation>()
    override val eventsNavigationEffect: SharedFlow<EventsNavigation> = _eventsNavigationEffect.asSharedFlow()

    override suspend fun collectNavigationEffect(handler: (BaseNavigation) -> Unit, destination: BaseNavigation) {
        when(destination) {
            is CenterNavigation -> _centerNavigationEffect.collectLatest(handler)
            is ProfileNavigation -> _profileNavigationEffect.collectLatest(handler)
            is EventsNavigation -> _eventsNavigationEffect.collectLatest(handler)
        }
    }

    override suspend fun navigate(destination: BaseNavigation) {
        when(destination) {
            is CenterNavigation -> _centerNavigationEffect.emit(destination)
            is ProfileNavigation -> _profileNavigationEffect.emit(destination)
            is EventsNavigation -> _eventsNavigationEffect.emit(destination)
        }
    }
}