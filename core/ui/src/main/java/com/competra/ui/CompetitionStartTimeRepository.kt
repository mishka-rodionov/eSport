package com.competra.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Хранит время старта активного соревнования в памяти.
 * Устанавливается при запуске сервиса и очищается при его остановке.
 * Позволяет секундомеру в UI продолжать работу после серверной синхронизации,
 * которая может обнулить startTime в локальной БД.
 */
class CompetitionStartTimeRepository {
    private val _startTimeMs = MutableStateFlow(0L)
    val startTimeMs: StateFlow<Long> = _startTimeMs.asStateFlow()

    fun set(startTimeMs: Long) { _startTimeMs.value = startTimeMs }
    fun clear() { _startTimeMs.value = 0L }
}
