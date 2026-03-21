package com.rodionov.center.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowSizeClass
import com.rodionov.center.presentation.draw.DrawParticipantsScreen
import com.rodionov.center.presentation.event_control.orienteering.OrienteeringEventControlScreen
import com.rodionov.center.presentation.get_chip.GetOrienteeringChipScreen
import com.rodionov.center.presentation.main.CenterScreen
import com.rodionov.center.presentation.kind_of_sport.KindOfSportScreen
import com.rodionov.center.presentation.orientiring_competition_create.*
import com.rodionov.center.presentation.participant_list.ParticipantListScreen
import com.rodionov.center.presentation.read_card.OrientReadCardScreen
import com.rodionov.center.presentation.results.OrienteeringCompetitionResultsScreen
import com.rodionov.data.navigation.CenterNavigation

/**
 * Граф навигации для раздела управления соревнованиями.
 */
fun NavGraphBuilder.centerGraph(windowSizeClass: WindowSizeClass) {
//    navigation<CenterNavigationGraph.CenterBaseRoute>(startDestination = CenterNavigationGraph.CenterRoute) {
    composable<CenterNavigation.CenterRoute> { CenterScreen() }
    composable<CenterNavigation.KindOfSportRoute> { KindOfSportScreen() }
    
    // Пошаговое создание соревнования
    composable<CenterNavigation.CommonCompetitionFieldRoute> { backStackEntry ->
        val route: CenterNavigation.CommonCompetitionFieldRoute = backStackEntry.toRoute()
        CommonCompetitionFieldScreen(competitionId = route.competitionId)
    }
    composable<CenterNavigation.RegistrationCompetitionFieldRoute> { backStackEntry ->
        val route: CenterNavigation.RegistrationCompetitionFieldRoute = backStackEntry.toRoute()
        RegistrationCompetitionFieldScreen(competitionId = route.competitionId)
    }
    composable<CenterNavigation.OrganizatorCompetitionFieldRoute> { backStackEntry ->
        val route: CenterNavigation.OrganizatorCompetitionFieldRoute = backStackEntry.toRoute()
        OrganizatorCompetitionFieldScreen(competitionId = route.competitionId)
    }
    composable<CenterNavigation.CreateDistanceRoute> { backStackEntry ->
        val route: CenterNavigation.CreateDistanceRoute = backStackEntry.toRoute()
        CreateDistanceScreen(competitionId = route.competitionId)
    }
    composable<CenterNavigation.CreateParticipantGroupRoute> { backStackEntry ->
        val route: CenterNavigation.CreateParticipantGroupRoute = backStackEntry.toRoute()
        CreateParticipantGroupScreen(competitionId = route.competitionId)
    }

    // Устаревший роут (для обратной совместимости во время перехода)
    composable<CenterNavigation.OrienteeringCreatorRoute> { backStackEntry ->
        val route: CenterNavigation.OrienteeringCreatorRoute = backStackEntry.toRoute()
        OrienteeringCompetitionCreator(competitionId = route.competitionId)
    }

    composable<CenterNavigation.OrienteeringEventControlRoute> {
        OrienteeringEventControlScreen(
            windowSizeClass = windowSizeClass
        )
    }
    composable<CenterNavigation.OrientReadCardRoute> { OrientReadCardScreen() }
    composable<CenterNavigation.ParticipantList> { ParticipantListScreen() }
    composable<CenterNavigation.DrawParticipants> { DrawParticipantsScreen() }
    composable<CenterNavigation.ParticipantResults> { OrienteeringCompetitionResultsScreen() }
    composable<CenterNavigation.GetOrienteeringChipRoute> { backStackEntry ->
        val route: CenterNavigation.GetOrienteeringChipRoute = backStackEntry.toRoute()
        GetOrienteeringChipScreen(competitionId = route.competitionId)
    }
//    }
}
