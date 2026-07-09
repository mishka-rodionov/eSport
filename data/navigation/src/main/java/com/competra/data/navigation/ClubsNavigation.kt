package com.competra.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Навигация для модуля клубов и команд.
 */
@Serializable
sealed class ClubsNavigation : BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    /** Базовый роут клубов (стартовый экран таба). */
    @Serializable
    data object ClubsBaseRoute : ClubsNavigation()

    /** Список/поиск клубов. */
    @Serializable
    data object ClubsListRoute : ClubsNavigation()

    /** Создание нового клуба. */
    @Serializable
    data object CreateClubRoute : ClubsNavigation()

    /**
     * Детали клуба: инфо, участники, команды. Редактирование клуба и создание команды —
     * inline-диалоги внутри этого экрана, отдельных роутов для них нет.
     */
    @Serializable
    data class ClubDetailRoute(val clubId: String) : ClubsNavigation()

    /** Входящие заявки на вступление в клуб (FOUNDER/ADMIN). */
    @Serializable
    data class ClubJoinRequestsRoute(val clubId: String) : ClubsNavigation()

    /** Собственные заявки текущего пользователя на вступление в клубы. */
    @Serializable
    data object MyJoinRequestsRoute : ClubsNavigation()

    /** Детали команды: участники, капитан. Редактирование команды — inline-диалог. */
    @Serializable
    data class TeamDetailRoute(val teamId: String) : ClubsNavigation()
}
