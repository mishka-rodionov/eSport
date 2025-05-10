package com.rodionov.data.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ProfileNavigation: BaseNavigation {
    @Serializable
    data object ProfileBaseRoute : ProfileNavigation()
    @Serializable
    data object MainProfileRoute : ProfileNavigation()
    @Serializable
    data object ProfileEditorRoute : ProfileNavigation()
    @Serializable
    data object AboutAppRoute : ProfileNavigation()
}