package com.rodionov.center.data.interactors

import com.rodionov.center.data.OrienteeringCreatorAction
import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.repository.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.OrienteeringCompetitionRemoteRepository

class OrienteeringCompetitionInteractor(
    private val orienteeringCompetitionLocalRepository: OrienteeringCompetitionLocalRepository,
    private val orienteeringCompetitionRemoteRepository: OrienteeringCompetitionRemoteRepository
) {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): OrienteeringCreatorAction {
        orienteeringCompetitionRemoteRepository.createCompetition(orienteeringCompetition)
            .onSuccess { competition ->
                orienteeringCompetitionLocalRepository.saveCompetition(competition).onSuccess {
                    return OrienteeringCreatorAction.SuccessfulCompetitionCreate
                }.onFailure {
                    return OrienteeringCreatorAction.FailedCompetitionCreate("Сохранено на сервере, ошибка локального сохранения")
                }
            }.onFailure {
                orienteeringCompetitionLocalRepository.saveCompetition(orienteeringCompetition).onSuccess {
                    return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, сохранено локально")
                }.onFailure {
                    return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка сервера, ошибка локального созранения")
                }
            }
        return OrienteeringCreatorAction.FailedCompetitionCreate("Ошибка")
    }

}