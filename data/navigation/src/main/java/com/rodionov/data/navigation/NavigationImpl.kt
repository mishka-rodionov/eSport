package com.rodionov.data.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Реализация интерфейса навигации.
 * Использует SharedFlow для передачи событий навигации между модулями.
 */
class NavigationImpl : Navigation {

    private val _centerNavigationEffect = MutableSharedFlow<CenterNavigation>()
    override val centerNavigationEffect: SharedFlow<CenterNavigation> =
        _centerNavigationEffect.asSharedFlow()
    
    private val _profileNavigationEffect = MutableSharedFlow<ProfileNavigation>()
    override val profileNavigationEffect: SharedFlow<ProfileNavigation> =
        _profileNavigationEffect.asSharedFlow()
    
    private val _eventsNavigationEffect = MutableSharedFlow<EventsNavigation>()
    override val eventsNavigationEffect: SharedFlow<EventsNavigation> =
        _eventsNavigationEffect.asSharedFlow()

    override var baseArgument: List<BaseArgument<*>>? = null

    override suspend fun collectNavigationEffect(
        handler: (BaseNavigation) -> Unit,
        destination: BaseNavigation
    ) {
        when (destination) {
            is CenterNavigation -> _centerNavigationEffect.collectLatest(handler)
            is ProfileNavigation -> _profileNavigationEffect.collectLatest(handler)
            is EventsNavigation -> _eventsNavigationEffect.collectLatest(handler)
        }
    }

    override suspend fun navigate(destination: BaseNavigation, argument: List<BaseArgument<*>>?) {
        baseArgument = argument
        when (destination) {
            is CenterNavigation -> _centerNavigationEffect.emit(destination)
            is ProfileNavigation -> _profileNavigationEffect.emit(destination)
            is EventsNavigation -> _eventsNavigationEffect.emit(destination)
        }
    }

    override suspend fun back() {
        // Логика перехода назад через эффекты навигации.
        // Передаем специальный роут или признак "назад".
        // В текущей реализации можно добавить BackRoute в BaseNavigation или 
        // обрабатывать напрямую через NavController.
        // Для примера используем emit в соответствующий поток.
        _centerNavigationEffect.emit(CenterNavigation.CenterRoute) // Упрощенно: возврат на главную
    }

    override fun <T> createArguments(vararg pairs: Pair<String, T?>): List<BaseArgument<*>> {
        val arguments = mutableListOf<BaseArgument<*>>()
        for ((key, value) in pairs) {
            arguments.add(BaseArgument(argName = key, argument = value))
        }
        return arguments
    }
}

/**
 * Расширение для получения аргументов из навигации.
 */
inline fun <reified T> Navigation.getArguments(name: String): T? {
    return when (val argument =
        baseArgument?.firstOrNull { baseArg -> baseArg.argName == name }?.argument) {
        is T -> argument
        else -> null
    }
}
