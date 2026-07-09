package com.competra.clubs.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.competra.clubs.presentation.detail.ClubDetailScreen
import com.competra.clubs.presentation.form.CreateClubScreen
import com.competra.clubs.presentation.join_requests.ClubJoinRequestsScreen
import com.competra.clubs.presentation.list.ClubsListScreen
import com.competra.clubs.presentation.my_requests.MyJoinRequestsScreen
import com.competra.clubs.presentation.team_detail.TeamDetailScreen
import com.competra.data.navigation.ClubsNavigation

/**
 * Граф навигации для раздела клубов и команд.
 */
fun NavGraphBuilder.clubsGraph() {
    composable<ClubsNavigation.ClubsBaseRoute> { ClubsListScreen() }
    composable<ClubsNavigation.ClubsListRoute> { ClubsListScreen() }
    composable<ClubsNavigation.CreateClubRoute> { CreateClubScreen() }

    composable<ClubsNavigation.ClubDetailRoute> { backStackEntry ->
        val route: ClubsNavigation.ClubDetailRoute = backStackEntry.toRoute()
        ClubDetailScreen(clubId = route.clubId)
    }

    composable<ClubsNavigation.ClubJoinRequestsRoute> { backStackEntry ->
        val route: ClubsNavigation.ClubJoinRequestsRoute = backStackEntry.toRoute()
        ClubJoinRequestsScreen(clubId = route.clubId)
    }

    composable<ClubsNavigation.MyJoinRequestsRoute> { MyJoinRequestsScreen() }

    composable<ClubsNavigation.TeamDetailRoute> { backStackEntry ->
        val route: ClubsNavigation.TeamDetailRoute = backStackEntry.toRoute()
        TeamDetailScreen(teamId = route.teamId)
    }
}
