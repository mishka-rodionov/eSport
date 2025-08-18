package com.rodionov.sportsenthusiast.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rodionov.center.navigation.centerGraph
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.events.navigation.eventsGraph
import com.rodionov.profile.navigation.profileNavigation
import com.rodionov.sportsenthusiast.BottomNavItem
import com.rodionov.sportsenthusiast.ui.theme.SportsEnthusiastTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(1))
        setContent {
            SportsEnthusiastTheme {
                // A surface container using the 'background' color from the theme
//                val navController = rememberNavController()
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
private fun MainScreen(viewModel: MainViewModel) {
    val navControllers = remember {
        BottomNavItem.all.associateWith { mutableStateOf<NavHostController?>(null) }
    }
    var selectedTab by rememberSaveable { mutableStateOf<String>(BottomNavItem.CompetitionList.route) }
//                viewModel.collectNavigationEffect(navController::navigate)
    Scaffold(
        bottomBar = {
//                        BottomNavBar(navController = navController, selectedTab = selectedTab)
            BottomNavigation {
                BottomNavItem.all.forEach { tab ->
                        BottomNavigationItem(
                            icon = { },
                            label = { Text(tab.title) },
                            selected = selectedTab == tab.route,
                            onClick = { selectedTab = tab.route }
                        )
                    }
            }
        }
    ) { innerPadding ->

        Box(Modifier.padding(innerPadding)) {
            // Все NavHost-ы присутствуют в иерархии, но только текущий видим
            BottomNavItem.all.forEach { tab ->
                val navController = hostController(navControllers, tab, viewModel)


                val isSelected = tab.route == selectedTab
                Log.d("LOG_TAG", "MainScreen: count")

                AnimatedVisibility(visible = isSelected, enter = fadeIn(), exit = fadeOut()) {
                    NavHost(
                        navController = navController,
                        startDestination = checkNavigation(tab),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (tab) {
                            BottomNavItem.Profile -> profileNavigation()
                            BottomNavItem.CompetitionList -> eventsGraph()
                            BottomNavItem.CompetitionConstructor -> centerGraph()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun hostController(
    navControllers: Map<BottomNavItem, MutableState<NavHostController?>>,
    tab: BottomNavItem,
    viewModel: MainViewModel
): NavHostController {
    Log.d("LOG_TAG", "hostController: $navControllers, $tab, $viewModel")
    if (navControllers[tab]?.value == null) {
        navControllers[tab]?.value = rememberNavController()
        viewModel.collectNavigationEffect({ route ->
            navControllers[tab]?.value!!.navigate(route = route) }, checkNavigation(tab))
    }
    val navController = navControllers[tab]?.value!!
    return navController
}

private fun checkNavigation(tab: BottomNavItem): BaseNavigation = when (tab) {
    BottomNavItem.Profile -> ProfileNavigation.MainProfileRoute
    BottomNavItem.CompetitionList -> EventsNavigation.EventsRoute
    BottomNavItem.CompetitionConstructor -> CenterNavigation.CenterRoute
}
