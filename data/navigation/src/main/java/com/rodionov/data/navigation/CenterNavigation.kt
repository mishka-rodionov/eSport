package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Навигация для раздела "Центр соревнований".
 * Содержит маршруты для управления событиями, создания соревнований и работы с участниками.
 */
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
    
    // Новые маршруты для пошагового создания соревнования
    @Serializable
    data class CommonCompetitionFieldRoute(val competitionId: Long? = null): CenterNavigation()
    @Serializable
    data class RegistrationCompetitionFieldRoute(val competitionId: Long): CenterNavigation()
    @Serializable
    data class OrganizatorCompetitionFieldRoute(val competitionId: Long): CenterNavigation()
    @Serializable
    data class CreateDistanceRoute(val competitionId: Long): CenterNavigation()
    @Serializable
    data class CreateParticipantGroupRoute(val competitionId: Long): CenterNavigation()

    @Serializable
    data class OrienteeringCreatorRoute(val competitionId: Long? = null): CenterNavigation()
    @Serializable
    data object OrienteeringEventControlRoute: CenterNavigation()
    @Serializable
    data object OrientReadCardRoute: CenterNavigation()
    @Serializable
    data object ParticipantList: CenterNavigation()
    @Serializable
    data object DrawParticipants: CenterNavigation()
    @Serializable
    data object ParticipantResults: CenterNavigation()
    @Serializable
    data class GetOrienteeringChipRoute(val competitionId: Long): CenterNavigation()
}
