package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class CenterNavigation: BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    @Serializable
    data object CenterBaseRoute: CenterNavigation()
    @Serializable
    data object CenterRoute: CenterNavigation()
    @Serializable
    data object KindOfSportRoute: CenterNavigation()
    @Serializable
    data object OrienteeringCreatorRoute: CenterNavigation()
    @Serializable
    data object OrienteeringEventControlRoute: CenterNavigation()
    @Serializable
    data object OrientReadCardRoute: CenterNavigation()
    @Serializable
    data object ParticipantList: CenterNavigation()
    @Serializable
    data object DrawParticipants: CenterNavigation()

}