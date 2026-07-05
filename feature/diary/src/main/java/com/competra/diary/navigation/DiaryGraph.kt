package com.competra.diary.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.competra.data.navigation.DiaryNavigation
import com.competra.diary.presentation.detail.WorkoutDetailScreen
import com.competra.diary.presentation.editor.WorkoutEditorScreen
import com.competra.diary.presentation.list.DiaryListScreen
import com.competra.diary.presentation.track.WorkoutTrackScreen
import com.competra.diary.presentation.tracking.LiveTrackingScreen
import com.competra.domain.models.diary.SportType

/**
 * Граф навигации для раздела "Тренировочный дневник".
 */
fun NavGraphBuilder.diaryGraph() {
    composable<DiaryNavigation.DiaryRoute> { DiaryListScreen() }

    composable<DiaryNavigation.WorkoutEditorRoute> { backStackEntry ->
        val route: DiaryNavigation.WorkoutEditorRoute = backStackEntry.toRoute()
        WorkoutEditorScreen(workoutId = route.workoutId)
    }

    composable<DiaryNavigation.WorkoutDetailRoute> { backStackEntry ->
        val route: DiaryNavigation.WorkoutDetailRoute = backStackEntry.toRoute()
        WorkoutDetailScreen(workoutId = route.workoutId)
    }

    composable<DiaryNavigation.LiveTrackingRoute> { backStackEntry ->
        val route: DiaryNavigation.LiveTrackingRoute = backStackEntry.toRoute()
        LiveTrackingScreen(sportType = SportType.valueOf(route.sportType))
    }

    composable<DiaryNavigation.WorkoutTrackRoute> { backStackEntry ->
        val route: DiaryNavigation.WorkoutTrackRoute = backStackEntry.toRoute()
        WorkoutTrackScreen(workoutId = route.workoutId)
    }
}
