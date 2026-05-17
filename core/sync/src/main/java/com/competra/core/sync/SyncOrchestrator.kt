package com.competra.core.sync

import android.util.Log
import com.competra.domain.exception.ConflictException
import com.competra.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.competra.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
 *  6. Deletes       (обратный порядок: results → participants → groups → distances → competitions)
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

    private val syncMutex = Mutex()

    suspend fun syncAll(): Outcome = syncMutex.withLock {
        var transientFailure = false

        transientFailure = transientFailure or syncCompetitions()
        transientFailure = transientFailure or syncDistances()
        transientFailure = transientFailure or syncGroups()
        transientFailure = transientFailure or syncParticipants()
        transientFailure = transientFailure or syncResults()
        transientFailure = transientFailure or syncDeletes()

        if (transientFailure) {
            throw IOException("Transient sync error")
        }

        var hasRemainingUnsynced = false
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedCompetitions().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedDistances().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedGroups().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedParticipants().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getUnsyncedResults().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getCompetitionsMarkedForDeletion().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getDistancesMarkedForDeletion().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getGroupsMarkedForDeletion().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getParticipantsMarkedForDeletion().isNotEmpty()
        hasRemainingUnsynced = hasRemainingUnsynced or localRepository.getResultsMarkedForDeletion().isNotEmpty()

        if (hasRemainingUnsynced) Outcome.Partial else Outcome.AllDone
    }

    /**
     * Удаления в обратном порядке зависимостей. Для каждой записи:
     *  - если remoteId есть → DELETE на сервере, при успехе физическое удаление локально;
     *  - если remoteId == null (никогда не синхронизировалась) → сразу удалить локально.
     */
    private suspend fun syncDeletes(): Boolean {
        var transient = false
        transient = transient or syncResultDeletes()
        transient = transient or syncParticipantDeletes()
        transient = transient or syncGroupDeletes()
        transient = transient or syncDistanceDeletes()
        transient = transient or syncCompetitionDeletes()
        return transient
    }

    private suspend fun syncResultDeletes(): Boolean {
        val marked = localRepository.getResultsMarkedForDeletion()
        var transient = false
        for (result in marked) {
            val remoteId = result.remoteId
            if (remoteId == null) {
                localRepository.purgeResultLocally(result.id)
                continue
            }
            val response = remoteRepository.deleteResultRemotely(remoteId)
            transient = transient or handleDelete(
                response = response,
                entityDescription = "result ${result.id}",
                onSuccess = { localRepository.purgeResultLocally(result.id) }
            )
        }
        return transient
    }

    private suspend fun syncParticipantDeletes(): Boolean {
        val marked = localRepository.getParticipantsMarkedForDeletion()
        var transient = false
        for (participant in marked) {
            // У участника server-id == client-id (UUID); если запись никогда не синхронизировалась,
            // remoteId всё равно null — просто удаляем локально.
            if (participant.remoteId == null) {
                localRepository.deleteParticipant(participant.id)
                continue
            }
            val response = remoteRepository.deleteParticipantRemotely(participant.id)
            transient = transient or handleDelete(
                response = response,
                entityDescription = "participant ${participant.id}",
                onSuccess = { localRepository.deleteParticipant(participant.id) }
            )
        }
        return transient
    }

    private suspend fun syncGroupDeletes(): Boolean {
        val marked = localRepository.getGroupsMarkedForDeletion()
        var transient = false
        for (group in marked) {
            val remoteId = group.remoteId
            if (remoteId == null) {
                localRepository.purgeGroupLocally(group.groupId)
                continue
            }
            val response = remoteRepository.deleteGroupRemotely(remoteId)
            transient = transient or handleDelete(
                response = response,
                entityDescription = "group ${group.groupId}",
                onSuccess = { localRepository.purgeGroupLocally(group.groupId) }
            )
        }
        return transient
    }

    private suspend fun syncDistanceDeletes(): Boolean {
        val marked = localRepository.getDistancesMarkedForDeletion()
        var transient = false
        for (distance in marked) {
            val remoteId = distance.remoteId
            if (remoteId == null) {
                localRepository.purgeDistanceLocally(distance.id)
                continue
            }
            val response = remoteRepository.deleteDistanceRemotely(remoteId)
            transient = transient or handleDelete(
                response = response,
                entityDescription = "distance ${distance.id}",
                onSuccess = { localRepository.purgeDistanceLocally(distance.id) }
            )
        }
        return transient
    }

    private suspend fun syncCompetitionDeletes(): Boolean {
        val marked = localRepository.getCompetitionsMarkedForDeletion()
        var transient = false
        for (competition in marked) {
            val remoteId = competition.competition.remoteId
            if (remoteId == null) {
                localRepository.deleteCompetition(competition.localCompetitionId)
                continue
            }
            val response = remoteRepository.deleteCompetitionRemotely(remoteId)
            transient = transient or handleDelete(
                response = response,
                entityDescription = "competition ${competition.localCompetitionId}",
                onSuccess = { localRepository.deleteCompetition(competition.localCompetitionId) }
            )
        }
        return transient
    }

    /**
     * Упрощённая обработка Result<Unit> для DELETE: на permanent error не помечаем
     * syncError (нет места — запись будет удалена либо тут же, либо при следующей попытке).
     * 404 трактуется как success — записи на сервере уже нет, и нам нужно лишь purge локально.
     */
    private suspend inline fun handleDelete(
        response: Result<Unit>,
        entityDescription: String,
        crossinline onSuccess: suspend () -> Unit
    ): Boolean {
        return when (val error = response.exceptionOrNull()) {
            null -> {
                onSuccess()
                false
            }
            is IOException -> {
                Log.w(TAG, "Transient delete failure for $entityDescription: ${error.message}")
                true
            }
            else -> {
                // 404/4xx: считаем за «уже удалено» и чистим локалку, чтобы не зацикливаться.
                Log.w(TAG, "Permanent delete error for $entityDescription: ${error.message} — purging locally")
                onSuccess()
                false
            }
        }
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
