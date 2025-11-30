package com.rodionov.center.data.interactors

import android.util.Log
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.orienteering.OrienteeringCompetitionRemoteRepository

class OrienteeringCompetitionInteractor(
    private val orienteeringCompetitionLocalRepository: OrienteeringCompetitionLocalRepository,
    private val orienteeringCompetitionRemoteRepository: OrienteeringCompetitionRemoteRepository
) {

    suspend fun saveCompetition(
        orienteeringCompetition: OrienteeringCompetition,
        participantGroups: List<ParticipantGroup>
    ): OrienteeringCreatorAction {
        //сетевой запрос на сохранение соревнования на сервере
        orienteeringCompetitionRemoteRepository.createCompetition(orienteeringCompetition)
            .onSuccess { competition ->
                //сохранение данных соревнования локально
                orienteeringCompetitionLocalRepository.saveCompetition(competition).fold({
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
                orienteeringCompetitionLocalRepository.saveCompetition(orienteeringCompetition)
                    .fold({
                        //локальное сохранение информации о группах участников
                        orienteeringCompetitionLocalRepository.saveParticipantsGroups(
                            participantGroups.map {
                                it.copy(
                                    competitionId = orienteeringCompetition.competitionId
                                )
                            })
                        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, сохранено локально")
                    }, {
                        //локальное сохранение информации о группах участников
                        orienteeringCompetitionLocalRepository.saveParticipantsGroups(
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

    suspend fun getCompetitionsByUserId(userId: String): Result<List<OrienteeringCompetition>> {
        // 1. Сначала пробуем получить из сети
        val remoteResult = orienteeringCompetitionRemoteRepository.getCompetitionsByUserid(userId)

        remoteResult.onSuccess { competitions ->
            // 2. Если сеть успешна — обновляем локальное хранилище
            orienteeringCompetitionLocalRepository.saveCompetitions(competitions)
            val compet = orienteeringCompetitionLocalRepository.getCompetitionsByUserid(userId).getOrNull()
            Log.d("LOG_TAG", "getCompetitionsByUserId: size = ${compet?.size}")
            return Result.success(compet ?: competitions)
        }

        // 3. Если сеть упала — достаём локальные данные
        val localResult = orienteeringCompetitionLocalRepository.getCompetitionsByUserid(userId)

        localResult.onSuccess { localCompetitions ->
            return Result.success(localCompetitions)
        }

        // 4. Если вообще всё сломалось — возвращаем ошибку сети
        return Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error"))
    }


    private suspend fun createParticipantsGroupsInfo(
        competitionId: Long,
        participantGroups: List<ParticipantGroup>
    ) {
        orienteeringCompetitionRemoteRepository.createParticipantsGroupsForCompetition(
            competitionId = competitionId,
            participantGroups = participantGroups
        ).onSuccess { participants ->
            orienteeringCompetitionLocalRepository.saveParticipantsGroups(participants)
        }.onFailure {
            orienteeringCompetitionLocalRepository.saveParticipantsGroups(participantGroups)
        }

    }

}