package com.rodionov.core.sync

import android.util.Log
import com.rodionov.domain.exception.ConflictException
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import java.io.IOException

/**
 * Оркестрирует выгрузку локальных изменений `:feature:center` на сервер.
 *
 * Pipeline (родители → дети):
 *  1. Competitions
 *  2. Distances     (требуют competition.remoteId)
 *  3. Groups        (требуют competition.remoteId и distance.remoteId)
 *  4. Participants  (требуют competition.remoteId и group.remoteId)
 *  5. Results       (требуют participant + competition + group)
 *
 * Если зависимость не имеет remoteId — запись пропускается в текущем пробеге и обработается
 * в следующем запуске Worker'а (после того как родительская сущность синхронизируется).
 *
 * Возвращает [Outcome.AllDone], если все isSynced=false записи выгружены, или
 * [Outcome.Partial], если осталось хоть что-то невыгруженное (Worker сделает retry).
 *
 * @throws IOException пробрасывается transient-ошибка для перезапуска Worker'а через backoff.
 */
class SyncOrchestrator(
    private val localRepository: OrienteeringCompetitionLocalRepository,
    private val remoteRepository: OrienteeringCompetitionRemoteRepository,
    private val conflictResolver: ConflictResolver
) {

    enum class Outcome { AllDone, Partial }

    suspend fun syncAll(): Outcome {
        var transientFailure = false

        transientFailure = transientFailure or syncCompetitions()
        transientFailure = transientFailure or syncDistances()
        transientFailure = transientFailure or syncGroups()
        transientFailure = transientFailure or syncParticipants()
        transientFailure = transientFailure or syncResults()

        if (transientFailure) {
            throw IOException("Transient sync error")
        }

        var hasRemainingUnsynced = false
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedCompetitions().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedDistances().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedGroups().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedParticipants().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedResults().isNotEmpty()

        return if (hasRemainingUnsynced) Outcome.Partial else Outcome.AllDone
    }

    private suspend fun syncCompetitions(): Boolean {
        val unsynced = localRepository.getUnsyncedCompetitions()
        var transient = false
        for (competition in unsynced) {
            val result = remoteRepository.createCompetition(competition)
            transient = transient or handleResult(
                result = result,
                entityDescription = "competition ${competition.localCompetitionId}",
                onSuccess = { server ->
                    localRepository.updateCompetition(server, markUnsynced = false)
                },
                onConflict = { payload ->
                    conflictResolver.applyCompetitionConflict(competition.localCompetitionId, payload)
                },
                onPermanentError = { msg ->
                    localRepository.updateCompetition(
                        competition.copy(competition = competition.competition.copy(syncError = msg)),
                        markUnsynced = false
                    )
                }
            )
        }
        return transient
    }

    private suspend fun syncDistances(): Boolean {
        val unsynced = localRepository.getUnsyncedDistances()
        if (unsynced.isEmpty()) return false

        val ready = unsynced.filter { dist ->
            getCompetitionRemoteId(dist.competitionId) != null
        }
        if (ready.isEmpty()) return false

        val byCompetition = ready.groupBy { it.competitionId }
        var transient = false
        for ((localCompetitionId, distances) in byCompetition) {
            val remoteCompetitionId = getCompetitionRemoteId(localCompetitionId) ?: continue
            val result = remoteRepository.publishDistancesForCompetition(
                remoteCompetitionId = remoteCompetitionId,
                localCompetitionId = localCompetitionId,
                distances = distances
            )
            transient = transient or handleResult(
                result = result,
                entityDescription = "distances for competition $localCompetitionId",
                onSuccess = { synced ->
                    synced.forEach { localRepository.updateDistance(it, markUnsynced = false) }
                },
                onConflict = { payload ->
                    // Сервер при batch-конфликте отдаёт одну запись — её и применяем.
                    distances.firstOrNull()?.let {
                        conflictResolver.applyDistanceConflict(it.id, localCompetitionId, payload)
                    }
                },
                onPermanentError = { msg ->
                    distances.forEach { localRepository.updateDistance(it.copy(syncError = msg), markUnsynced = false) }
                }
            )
        }
        return transient
    }

    private suspend fun syncGroups(): Boolean {
        val unsynced = localRepository.getUnsyncedGroups()
        if (unsynced.isEmpty()) return false

        val ready = unsynced.filter { group ->
            getCompetitionRemoteId(group.competitionId) != null &&
                getDistanceRemoteId(group.distanceId) != null
        }
        if (ready.isEmpty()) return false

        val byCompetition = ready.groupBy { it.competitionId }
        var transient = false
        for ((localCompetitionId, groups) in byCompetition) {
            val remoteCompetitionId = getCompetitionRemoteId(localCompetitionId) ?: continue
            val groupsWithRemoteDistance = groups.map { g ->
                val remoteDistance = getDistanceRemoteId(g.distanceId) ?: return@map g
                g.copy(distanceId = remoteDistance)
            }
            val result = remoteRepository.publishGroupsForCompetition(remoteCompetitionId, groupsWithRemoteDistance)
            transient = transient or handleResult(
                result = result,
                entityDescription = "groups for competition $localCompetitionId",
                onSuccess = { synced ->
                    synced.forEach { localRepository.updateParticipantGroup(it, markUnsynced = false) }
                },
                onConflict = { payload ->
                    groups.firstOrNull()?.let {
                        conflictResolver.applyGroupConflict(it.groupId, localCompetitionId, payload)
                    }
                },
                onPermanentError = { msg ->
                    groups.forEach { localRepository.updateParticipantGroup(it.copy(syncError = msg), markUnsynced = false) }
                }
            )
        }
        return transient
    }

    private suspend fun syncParticipants(): Boolean {
        val unsynced = localRepository.getUnsyncedParticipants()
        if (unsynced.isEmpty()) return false

        val ready = unsynced.filter { p ->
            getCompetitionRemoteId(p.competitionId) != null &&
                getGroupRemoteId(p.groupId) != null
        }
        if (ready.isEmpty()) return false

        val mapped = ready.mapNotNull { p ->
            val remoteCompetitionId = getCompetitionRemoteId(p.competitionId) ?: return@mapNotNull null
            val remoteGroupId = getGroupRemoteId(p.groupId) ?: return@mapNotNull null
            p to p.copy(competitionId = remoteCompetitionId, groupId = remoteGroupId)
        }

        var transient = false
        val result = remoteRepository.saveParticipants(mapped.map { it.second })
        transient = transient or handleResult(
            result = result,
            entityDescription = "participants batch",
            onSuccess = {
                ready.forEach {
                    localRepository.updateParticipants(
                        listOf(it.copy(isSynced = true, syncError = null)),
                        markUnsynced = false
                    )
                }
            },
            onConflict = { payload ->
                ready.firstOrNull()?.let {
                    conflictResolver.applyParticipantConflict(it.competitionId, it.groupId, payload)
                }
            },
            onPermanentError = { msg ->
                ready.forEach {
                    localRepository.updateParticipants(
                        listOf(it.copy(syncError = msg)),
                        markUnsynced = false
                    )
                }
            }
        )
        return transient
    }

    private suspend fun syncResults(): Boolean {
        val unsynced = localRepository.getUnsyncedResults()
        if (unsynced.isEmpty()) return false

        val ready = unsynced.filter { r ->
            getCompetitionRemoteId(r.competitionId) != null &&
                getGroupRemoteId(r.groupId) != null
        }
        if (ready.isEmpty()) return false

        var transient = false
        for (result in ready) {
            val remoteCompetitionId = getCompetitionRemoteId(result.competitionId) ?: continue
            val remoteGroupId = getGroupRemoteId(result.groupId) ?: continue
            val mapped = result.copy(competitionId = remoteCompetitionId, groupId = remoteGroupId)
            val response = remoteRepository.saveResult(mapped)
            transient = transient or handleResult(
                result = response,
                entityDescription = "result ${result.id}",
                onSuccess = { server ->
                    localRepository.updateResults(
                        listOf(result.copy(isSynced = true, syncError = null, serverUpdatedAt = server.serverUpdatedAt)),
                        markUnsynced = false
                    )
                },
                onConflict = { payload ->
                    conflictResolver.applyResultConflict(result.id, result.competitionId, result.groupId, payload)
                },
                onPermanentError = { msg ->
                    localRepository.updateResults(
                        listOf(result.copy(syncError = msg)),
                        markUnsynced = false
                    )
                }
            )
        }
        return transient
    }

    /**
     * Унифицированная обработка Result<T>:
     *  - success → [onSuccess]
     *  - ConflictException → [onConflict] с payload (server-wins применяется в ConflictResolver)
     *  - IOException → возвращается true (transient, Worker повторит попытку)
     *  - другое → [onPermanentError] с сообщением
     *
     * @return true, если ошибка transient.
     */
    private suspend inline fun <T> handleResult(
        result: Result<T>,
        entityDescription: String,
        crossinline onSuccess: suspend (T) -> Unit,
        crossinline onConflict: suspend (String?) -> Unit,
        crossinline onPermanentError: suspend (String) -> Unit
    ): Boolean {
        return when (val error = result.exceptionOrNull()) {
            null -> {
                onSuccess(result.getOrThrow())
                false
            }
            is IOException -> {
                Log.w(TAG, "Transient sync failure for $entityDescription: ${error.message}")
                true
            }
            is ConflictException -> {
                Log.i(TAG, "Conflict 409 for $entityDescription, applying server-wins")
                onConflict(error.serverPayload)
                false
            }
            else -> {
                Log.w(TAG, "Permanent sync error for $entityDescription: ${error.message}")
                onPermanentError(error.message ?: error::class.simpleName.orEmpty())
                false
            }
        }
    }

    private suspend fun getCompetitionRemoteId(localId: Long): Long? =
        localRepository.getCompetition(localId).getOrNull()?.competition?.remoteId

    private suspend fun getDistanceRemoteId(localId: Long): Long? =
        localRepository.getDistanceById(localId).getOrNull()?.remoteId

    private suspend fun getGroupRemoteId(localId: Long): Long? =
        localRepository.getParticipantGroup(localId).getOrNull()?.remoteId

    companion object {
        private const val TAG = "SyncOrchestrator"
    }
}
