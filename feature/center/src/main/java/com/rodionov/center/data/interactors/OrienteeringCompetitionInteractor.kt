package com.rodionov.center.data.interactors

import com.rodionov.center.data.OrienteeringCreatorAction
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