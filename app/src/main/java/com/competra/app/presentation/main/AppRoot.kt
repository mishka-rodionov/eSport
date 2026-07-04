package com.competra.app.presentation.main

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.competra.app.presentation.splash.SplashScreen
import com.competra.domain.models.onboarding.OnboardingSource
import com.competra.domain.repository.onboarding.OnboardingRepository
import com.competra.onboarding.presentation.OnboardingScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class AppPhase { SPLASH, ONBOARDING, MAIN }

/**
 * Корневой composable: Splash -> (если онбординг не просмотрен) Onboarding -> обычный MainScreen.
 * Показ онбординга здесь не зависит от авторизации пользователя.
 */
@Composable
fun AppRoot(viewModel: MainViewModel) {
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val onboardingRepository = koinInject<OnboardingRepository>()
    val scope = rememberCoroutineScope()
    var phase by rememberSaveable { mutableStateOf(AppPhase.SPLASH) }

    when (phase) {
        AppPhase.SPLASH -> SplashScreen(onFinished = {
            scope.launch {
                phase = if (onboardingRepository.isSeen()) AppPhase.MAIN else AppPhase.ONBOARDING
            }
        })

        AppPhase.ONBOARDING -> OnboardingScreen(
            source = OnboardingSource.FIRST_LAUNCH,
            viewModelKey = "onboarding_first_launch",
            onFinished = { phase = AppPhase.MAIN }
        )

        AppPhase.MAIN -> {
            RequestInitialPermissions()
            MainScreen(viewModel, widthSizeClass)
        }
    }
}

/**
 * Запрос начальных разрешений: уведомления и местоположение.
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
        permissions.entries.forEach {
            Log.d("PERMISSIONS", "${it.key} granted: ${it.value}")
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissionsToRequest.toTypedArray())
    }
}
