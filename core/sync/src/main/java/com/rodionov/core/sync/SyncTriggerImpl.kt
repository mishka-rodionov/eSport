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
 * Belt-and-suspenders: на каждом запросе и на любой неудаче также ставится UniqueWork через
 * [SyncBootstrap]. `ExistingWorkPolicy.KEEP` гарантирует идемпотентность, а Worker подхватит
 * незавершённые записи, если процесс умрёт в середине immediate-попытки.
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
        SyncBootstrap.enqueue(context)
    }
}
