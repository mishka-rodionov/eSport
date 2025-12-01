package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ProfileNavigation: BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    @Serializable
    data object ProfileBaseRoute : ProfileNavigation()
    @Serializable
    data object MainProfileRoute : ProfileNavigation()
    @Serializable
    data object ProfileEditorRoute : ProfileNavigation()
    @Serializable
    data object AboutAppRoute : ProfileNavigation()
    @Serializable
    data object AuthRoute : ProfileNavigation()
    @Serializable
    data object AuthCodeRoute : ProfileNavigation()
    @Serializable
    data object RegistrationRoute : ProfileNavigation()
}