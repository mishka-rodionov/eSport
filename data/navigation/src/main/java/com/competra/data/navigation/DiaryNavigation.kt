package com.competra.data.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Навигация для раздела "Тренировочный дневник".
 */
@Serializable
sealed class DiaryNavigation : BaseNavigation {

    @Transient
    @Contextual
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null

    @Serializable
    data object DiaryRoute : DiaryNavigation()

    @Serializable
    data class WorkoutEditorRoute(val workoutId: Long? = null) : DiaryNavigation()

    @Serializable
    data class WorkoutDetailRoute(val workoutId: Long) : DiaryNavigation()
}
