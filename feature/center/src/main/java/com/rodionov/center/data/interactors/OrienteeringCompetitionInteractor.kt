package com.rodionov.center.data.interactors

import android.util.Log
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository

class OrienteeringCompetitionInteractor(
    private val localRepository: OrienteeringCompetitionLocalRepository,
    private val remoteRepository: OrienteeringCompetitionRemoteRepository
) {

    suspend fun saveCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ): OrienteeringCreatorAction {
        //сетевой запрос на сохранение соревнования на сервере
        remoteRepository.createCompetition(orienteeringCompetition)
            .onSuccess { competition ->
                //сохранение данных соревнования локально
                localRepository.saveCompetition(competition).fold({
                    //сохранения данных по группам участников на сервере
                    createParticipantsGroupsInfo(
                        competitionId = competition.competitionId,
                        participantGroups = participantGroups
                    )
                    return OrienteeringCreatorAction.SuccessfulCompetitionCreate
                }, {
                    //сохранения данных по группам участников на сервере
                    createParticipantsGroupsInfo(
                        competitionId = competition.competitionId,
                        participantGroups = participantGroups
                    )
                    return OrienteeringCreatorAction.FailedCompetitionCreate("Сохранено на сервере, ошибка локального сохранения")
                }
                )
            }.onFailure {
                //локальное сохранение информации о соревновании
                localRepository.saveCompetition(orienteeringCompetition)
                    .fold({
                        //локальное сохранение информации о группах участников
                        localRepository.saveParticipantsGroups(
                            participantGroups.map {
                                it.copy(
                                    competitionId = orienteeringCompetition.competitionId
                                )
                            })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, сохранено локально")
                    }, {
                        //локальное сохранение информации о группах участников
                        localRepository.saveParticipantsGroups(
                            participantGroups.map {
                                it.copy(
                                    competitionId = orienteeringCompetition.competitionId
                                )
                            })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, ошибка локального созранения")
                    })

            }
        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
    }

    suspend fun getCompetition(competitionId: Long): OrienteeringCompetition? {
        return localRepository.getCompetition(competitionId).getOrNull()
    }

    suspend fun getCompetitionsByUserId(userId: String): Result<List<OrienteeringCompetition>> {
        // 1. Сначала пробуем получить из сети
        val remoteResult = remoteRepository.getCompetitionsByUserid(userId)

        remoteResult.onSuccess { competitions ->
            // 2. Если сеть успешна — обновляем локальное хранилище
            localRepository.saveCompetitions(competitions)
            val compet = localRepository.getCompetitionsByUserid(userId).getOrNull()
            Log.d("LOG_TAG", "getCompetitionsByUserId: size = ${compet?.size}")
            return Result.success(compet ?: competitions)
        }

        // 3. Если сеть упала — достаём локальные данные
        val localResult = localRepository.getCompetitionsByUserid(userId)

        localResult.onSuccess { localCompetitions ->
            return Result.success(localCompetitions)
        }

        // 4. Если вообще всё сломалось — возвращаем ошибку сети
        return Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error"))
    }

    suspend fun saveParticipant(participant: OrienteeringParticipant): OrienteeringParticipant? {
        return localRepository.saveParticipant(participant).getOrNull()
    }

    suspend fun updateParticipants(participants: List<OrienteeringParticipant>) {
        localRepository.updateParticipants(participants = participants)
    }


    private suspend fun createParticipantsGroupsInfo(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ) {
        remoteRepository.createParticipantsGroupsForCompetition(
            competitionId = competitionId,
            participantGroups = participantGroups
        ).onSuccess { participants ->
            localRepository.saveParticipantsGroups(participants)
        }.onFailure {
            localRepository.saveParticipantsGroups(participantGroups)
        }

    }

    suspend fun getParticipants(competitionId: Long): Result<List<OrienteeringParticipant>> {
        return localRepository.getParticipants(competitionId = competitionId)
    }

    suspend fun getParticipantByChipNumber(
        competitionId: Long,
        chipNumber: Int
    ): Result<OrienteeringParticipant> {
        return localRepository.getParticipantByChipNumber(
            competitionId = competitionId,
            chipNumber = chipNumber
        )
    }

    suspend fun getParticipantGroup(groupId: Long): Result<ParticipantGroup> {
        return localRepository.getParticipantGroup(groupId)
    }

}