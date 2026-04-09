package com.rodionov.data.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Интерфейс навигации приложения.
 * Управляет переходами между экранами в разных модулях.
 */
sealed interface Navigation {

    val centerNavigationEffect: SharedFlow<CenterNavigation>
    val profileNavigationEffect: SharedFlow<ProfileNavigation>
    val eventsNavigationEffect: SharedFlow<EventsNavigation>
    
    /** Общий поток эффектов навигации для всего приложения (например, BackRoute) */
    val baseNavigationEffect: SharedFlow<BaseNavigation>

    /** Поток переключения табов нижней навигации. Эмитит route таба (см. [TabRoutes]). */
    val switchTabEffect: SharedFlow<String>

    var baseArgument: List<BaseArgument<*>>?

    /**
     * Подписывается на эффекты навигации для указанного направления.
     */
    suspend fun collectNavigationEffect(handler: (BaseNavigation) -> Unit, destination: BaseNavigation)

    /**
     * Выполняет переход на указанный роут.
     */
    suspend fun navigate(destination: BaseNavigation, argument: List<BaseArgument<*>>? = null)

    /**
     * Переключает активный таб нижней навигации.
     * @param tabRoute route таба (см. [TabRoutes]).
     */
    suspend fun switchTab(tabRoute: String)

    /**
     * Выполняет переход назад (возврат на предыдущий экран).
     */
    suspend fun back()

    /**
     * Создает список аргументов для передачи между экранами.
     */
    fun <T> createArguments(vararg pairs: Pair<String, T?>): List<BaseArgument<*>>

}
