package com.rodionov.sportsenthusiast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodionov.center.navigation.CenterNavigationGraph
import com.rodionov.center.navigation.centerGraph
import com.rodionov.news.navigation.EventsNavigationGraph
import com.rodionov.news.navigation.eventsGraph
import com.rodionov.profile.navigation.ProfileNavigationGraph
import com.rodionov.profile.navigation.profileNavigation
import com.rodionov.sportsenthusiast.ui.theme.SportsEnthusiastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            SportsEnthusiastTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavigationHost(innerPadding, navController)
                }

//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Column(verticalArrangement = Arrangement.Bottom) {
//                        Text(text = "Test123")
//                        NavigationHost()
//                    }
//                }
            }
        }
    }
}

@Composable
fun NavigationHost(innerPadding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = EventsNavigationGraph.EventsBaseRoute,
        modifier = Modifier.padding(innerPadding)
    ) {
//        ProfileNavigation(navigationProvider = this.provider).createNestedGraph(startDestination = BottomNavItem.Profile.route)
//        composable(BottomNavItem.Profile.route) { ProfileScreen() }
        profileNavigation()
        eventsGraph()
        centerGraph()
//        composable(BottomNavItem.CompetitionConstructor.route) { /* Search Screen UI */ }
//        composable<BottomNavItem.CompetitionList> { NewsScreen() }
    }
//    BottomNavBar(navController = navController)
}

@Composable
fun BottomNavBar(navController: NavController) {
    BottomNavigation(
        modifier = Modifier.wrapContentHeight(),

        ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        BottomNavItem::class.sealedSubclasses.mapNotNull { it.objectInstance }.forEach { item ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(when(item) {
                        is BottomNavItem.Profile -> ProfileNavigationGraph.ProfileBaseRoute
                        is BottomNavItem.CompetitionList -> EventsNavigationGraph.EventsBaseRoute
                        is BottomNavItem.CompetitionConstructor -> CenterNavigationGraph.CenterBaseRoute
                    }) {
//                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { },
                label = { Text(text = item.title) })

        }
    }
}