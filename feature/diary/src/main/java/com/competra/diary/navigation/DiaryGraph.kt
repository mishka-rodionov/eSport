package com.competra.diary.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.competra.data.navigation.DiaryNavigation
import com.competra.diary.presentation.detail.WorkoutDetailScreen
import com.competra.diary.presentation.editor.WorkoutEditorScreen
import com.competra.diary.presentation.list.DiaryListScreen

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
}
