package com.rodionov.center.data.interactors

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.repository.OrienteeringCompetitionLocalRepository
import com.rodionov.domain.repository.OrienteeringCompetitionRemoteRepository

class OrienteeringCompetitionInteractor(
    private val orienteeringCompetitionLocalRepository: OrienteeringCompetitionLocalRepository,
    private val orienteeringCompetitionRemoteRepository: OrienteeringCompetitionRemoteRepository
) {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition) {
        orienteeringCompetitionRemoteRepository.createCompetition(orienteeringCompetition)
            .onSuccess { competition ->
                orienteeringCompetitionLocalRepository.saveCompetition(competition).onSuccess {

                }.onFailure {

                }
            }.onFailure {
                orienteeringCompetitionLocalRepository.saveCompetition(orienteeringCompetition).onSuccess {

                }.onFailure {

                }
            }
    }

}