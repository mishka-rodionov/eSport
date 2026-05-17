package com.competra.data.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Реализация интерфейса навигации.
 * Использует SharedFlow для передачи событий навигации между модулями.
 */
class NavigationImpl : Navigation {

    private val _baseNavigationEffect = MutableSharedFlow<BaseNavigation>()
    override val baseNavigationEffect: SharedFlow<BaseNavigation> =
        _baseNavigationEffect.asSharedFlow()

    /**
     * Общий поток обычных навигационных событий. Слушается активным табом
     * независимо от типа [destination]: NavController сам выбирает зарегистрированный
     * в своём графе composable. Это позволяет одному графу (например, профильному)
     * перехватывать роуты других модулей (например, [EventsNavigation.EventDetailsRoute]),
     * если соответствующий подграф к нему подключён.
     */
    private val _navigationEffect = MutableSharedFlow<BaseNavigation>()

    private val _switchTabEffect = MutableSharedFlow<String>()
    override val switchTabEffect: SharedFlow<String> = _switchTabEffect.asSharedFlow()

    override var baseArgument: List<BaseArgument<*>>? = null

    override suspend fun collectNavigationEffect(
        handler: (BaseNavigation) -> Unit,
        destination: BaseNavigation
    ) {
        if (destination is BackRoute) {
            _baseNavigationEffect.collectLatest(handler)
        } else {
            _navigationEffect.collectLatest(handler)
        }
    }

    override suspend fun navigate(destination: BaseNavigation, argument: List<BaseArgument<*>>?) {
        baseArgument = argument
        if (destination is BackRoute) {
            _baseNavigationEffect.emit(destination)
        } else {
            _navigationEffect.emit(destination)
        }
    }

    override suspend fun switchTab(tabRoute: String) {
        _switchTabEffect.emit(tabRoute)
    }

    /**
     * Выполняет переход назад.
     * Эмитит специальный роут BackRoute в общий поток навигации.
     */
    override suspend fun back() {
        // Изменено: теперь вместо перехода на CenterRoute мы эмитим BackRoute
        _baseNavigationEffect.emit(BackRoute)
        // _centerNavigationEffect.emit(CenterNavigation.CenterRoute)
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
