package com.rodionov.data.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

class NavigationImpl: Navigation {

    private val _navigationEffect = MutableSharedFlow<BaseNavigation>()
    override val navigationEffect: SharedFlow<BaseNavigation> = _navigationEffect.asSharedFlow()

    override suspend fun collectNavigationEffect(handler: (BaseNavigation) -> Unit) {
        navigationEffect.collectLatest(handler)
    }

    override suspend fun navigate(destination: BaseNavigation) {
        _navigationEffect.emit(destination)
    }
}