package com.rodionov.sportsenthusiast.presentation.main

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.rodionov.center.navigation.centerGraph
import com.rodionov.data.navigation.BackRoute
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.ProfileNavigation
import com.rodionov.events.navigation.eventsGraph
import com.rodionov.profile.navigation.profileNavigation
import com.rodionov.sportsenthusiast.BottomNavItem
import com.rodionov.sportsenthusiast.service.CompetitionForegroundService
import com.rodionov.sportsenthusiast.ui.theme.SportsEnthusiastTheme
import com.rodionov.ui.CompetitionServiceCommand
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Главная Activity приложения.
 * Является точкой входа, управляет жизненным циклом NFC-адаптера, foreground-сервисом соревнования
 * и запросом необходимых разрешений.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    private val nfcPendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this, 0,
            Intent(
                this,
                this.javaClass
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.setNfcAdapter(NfcAdapter.getDefaultAdapter(this))
        setContent {
            val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            SportsEnthusiastTheme {
                // Запрос разрешений при старте приложения
                RequestInitialPermissions()
                
                MainScreen(viewModel, widthSizeClass)
            }
        }
        observeServiceCommands()
    }

    /**
     * Компонент для запроса начальных разрешений: уведомления и местоположение.
     */
    @Composable
    private fun RequestInitialPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Обработка результатов, если требуется логгирование или спец. логика
            permissions.entries.forEach {
                Log.d("PERMISSIONS", "${it.key} granted: ${it.value}")
            }
        }

        LaunchedEffect(Unit) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }

    /**
     * Подписка на команды управления сервисом (запуск/остановка).
     */
    private fun observeServiceCommands() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serviceCommands.collect { cmd ->
                    when (cmd) {
                        is CompetitionServiceCommand.Start ->
                            startForegroundService(
                                CompetitionForegroundService.startIntent(
                                    this@MainActivity,
                                    cmd.competitionId,
                                    cmd.startTimeMs
                                )
                            )
                        is CompetitionServiceCommand.Stop ->
                            stopService(
                                Intent(this@MainActivity, CompetitionForegroundService::class.java)
                            )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.enableReaderMode(this, viewModel::onNewTagDetected)
    }

    override fun onPause() {
        super.onPause()
        viewModel.disableReaderMode(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        }
        tag?.let(viewModel::onNewTagDetected)
    }
}

/**
 * Основной экран приложения с нижней навигацией.
 * 
 * @param viewModel Вьюмодель управления общим состоянием.
 * @param windowSizeClass Параметры размера экрана для адаптивной верстки.
 */
@Composable
private fun MainScreen(viewModel: MainViewModel, windowSizeClass: WindowSizeClass) {
    BottomNavItem.all // не удалять, падает при первом tab.route
    var selectedTab by rememberSaveable { mutableStateOf<String>(BottomNavItem.CompetitionList.route) }
    val saveableStateHolder = rememberSaveableStateHolder()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanEvent by viewModel.currentScanEvent.collectAsState()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                BottomNavItem.all.forEach { tab ->
                    val isSelected = selectedTab == tab.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(tab.iconRes),
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { 
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        },
                        selected = isSelected,
                        onClick = { selectedTab = tab.route },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            Modifier
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            BottomNavItem.all.forEach { tab ->

                val isSelected = tab.route == selectedTab

                AnimatedVisibility(visible = isSelected, enter = fadeIn(), exit = fadeOut()) {
                    saveableStateHolder.SaveableStateProvider(tab.route) {
                        val navController = rememberNavController()

                        val isSelectedTab = selectedTab == tab.route
                        LaunchedEffect(navController, isSelectedTab) {
                            if (isSelectedTab) {
                                lifecycleOwner.lifecycleScope.launch {
                                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        
                                        launch {
                                            viewModel.baseNavigationEffect.collectLatest { route ->
                                                if (route is BackRoute) {
                                                    navController.popBackStack()
                                                }
                                            }
                                        }

                                        launch {
                                            viewModel.collectNavigationEffect(
                                                navigationHandler = { route ->
                                                    val navBuilder = route.navOptionsBuilder
                                                    if (navBuilder != null) {
                                                        navController.navigate(route, navBuilder)
                                                    } else {
                                                        navController.navigate(route = route)
                                                    }
                                                    route.navOptionsBuilder = null
                                                },
                                                destination = checkNavigation(tab)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        NavHost(
                            navController = navController,
                            startDestination = checkNavigation(tab),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (tab) {
                                BottomNavItem.Profile -> profileNavigation()
                                BottomNavItem.CompetitionList -> eventsGraph()
                                BottomNavItem.CompetitionConstructor -> centerGraph(windowSizeClass)
                            }
                        }
                    }
                }
            }

            NfcScanBanner(
                event = scanEvent,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
            )
        }
    }
}

/**
 * Определяет начальный роут для каждого таба нижней навигации.
 */
private fun checkNavigation(tab: BottomNavItem): BaseNavigation = when (tab) {
    BottomNavItem.Profile -> ProfileNavigation.MainProfileRoute
    BottomNavItem.CompetitionList -> EventsNavigation.EventsRoute
    BottomNavItem.CompetitionConstructor -> CenterNavigation.CenterRoute
}
