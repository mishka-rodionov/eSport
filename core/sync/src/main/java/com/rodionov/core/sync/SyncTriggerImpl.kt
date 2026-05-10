package com.rodionov.core.sync

import android.content.Context
import com.rodionov.domain.sync.SyncTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Singleton-реализация [SyncTrigger]: после каждой локальной мутации в `:feature:center`
 * пытается «бесшумно» прогнать [SyncOrchestrator.syncAll] в собственном app-scope.
 *
 * Coalescing обеспечивает [Channel.CONFLATED] — серия `trySend` коллапсирует в один сигнал,
 * burst из десятков мутаций (жеребьёвка) даёт максимум один–два пробега `syncAll`.
 *
 * UniqueWork через [SyncBootstrap] ставится только при сбое immediate-попытки
 * (IOException/Partial) — Worker дотянет данные, когда сеть восстановится либо когда
 * процесс перезапустится. При успехе immediate-sync POST уже выполнен, и второй проход
 * Worker'а не нужен.
 */
class SyncTriggerImpl(
    private val context: Context,
    private val orchestrator: SyncOrchestrator,
) : SyncTrigger {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val requests = Channel<Unit>(Channel.CONFLATED)

    init {
        scope.launch {
            for (signal in requests) {
                runCatching { orchestrator.syncAll() }
                    .onSuccess { outcome ->
                        if (outcome == SyncOrchestrator.Outcome.Partial) {
                            SyncBootstrap.enqueue(context)
                        }
                    }
                    .onFailure { SyncBootstrap.enqueue(context) }
            }
        }
    }

    override fun requestImmediateSync() {
        requests.trySend(Unit)
    }
}
