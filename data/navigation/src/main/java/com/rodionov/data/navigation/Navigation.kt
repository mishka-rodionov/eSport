package com.rodionov.data.navigation

import kotlinx.coroutines.flow.SharedFlow

sealed interface Navigation {

    val navigationEffect: SharedFlow<BaseNavigation>

    suspend fun collectNavigationEffect(handler: (BaseNavigation) -> Unit)

    suspend fun navigate(destination: BaseNavigation)

}