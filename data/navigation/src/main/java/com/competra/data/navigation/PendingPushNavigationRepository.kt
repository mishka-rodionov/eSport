package com.competra.data.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Отложенный переход по тапу на push-уведомление — сохраняется вместе с переключением
 * таба на Events (см. [Navigation.switchTab]), т.к. `Navigation.navigate` эмитит в SharedFlow
 * без буфера: если NavHost целевого таба ещё не подписан на события (таб только переключается),
 * событие будет потеряно. StateFlow здесь конфлейтит и отдаёт последнее значение новому
 * подписчику, поэтому гонка отсутствует независимо от того, что раньше — set() или подписка.
 */
class PendingPushNavigationRepository {

    private val _pending = MutableStateFlow<BaseNavigation?>(null)
    val pending: StateFlow<BaseNavigation?> = _pending.asStateFlow()

    fun set(route: BaseNavigation) {
        _pending.value = route
    }

    fun clear() {
        _pending.value = null
    }
}
