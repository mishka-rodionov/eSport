package com.rodionov.domain.sync

/**
 * Контракт «бесшумной» немедленной попытки синхронизации с сервером.
 *
 * Вызывается из Interactor-слоя сразу после успешной локальной мутации.
 * Реализация должна быть fire-and-forget: исключения не пробрасывает,
 * UI про lifecycle ничего не знает. При неудаче запись остаётся `isSynced=false`,
 * а fallback'ом служит уже существующий `SyncCenterWorker`.
 */
interface SyncTrigger {
    fun requestImmediateSync()
}
