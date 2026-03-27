package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder

/**
 * Базовый интерфейс для всех направлений навигации в приложении.
 */
interface BaseNavigation {
    /**
     * Конфигурация опций навигации (например, popUpTo, launchSingleTop).
     */
    var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)?
}

/**
 * Специальный роут для выполнения операции возврата назад в навигационном стеке.
 */
data object BackRoute : BaseNavigation {
    override var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
}
