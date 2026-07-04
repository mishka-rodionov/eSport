package com.competra.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.competra.resources.R
import kotlinx.serialization.Serializable

/**
 * Описание элементов нижней навигации приложения.
 * 
 * @property route Уникальный идентификатор маршрута.
 * @property title Заголовок вкладки.
 * @property iconRes Ресурс иконки (из модуля :core:resources).
 */
@Serializable
sealed class BottomNavItem(
    val route: String, 
    val title: String, 
    val iconRes: Int
) {
    @Serializable
    data object CompetitionList : BottomNavItem(
        route = "CompetitionList", 
        title = "События",
        iconRes = R.drawable.ic_date_range_24px
    )

    @Serializable
    data object CompetitionConstructor : BottomNavItem(
        route = "CompetitionConstructor", 
        title = "Управление",
        iconRes = R.drawable.ic_location_on_24px // Используем как символ карты/места управления
    )

    @Serializable
    data object Profile : BottomNavItem(
        route = "Profile",
        title = "Профиль",
        iconRes = R.drawable.ic_account_circle_24px
    )

    @Serializable
    data object Diary : BottomNavItem(
        route = "Diary",
        title = "Дневник",
        iconRes = R.drawable.ic_directions_run_24px
    )

    companion object {
        val all = listOf(CompetitionList, CompetitionConstructor, Diary, Profile)
    }
}
