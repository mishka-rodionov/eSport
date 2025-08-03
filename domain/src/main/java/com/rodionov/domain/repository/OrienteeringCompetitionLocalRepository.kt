package com.rodionov.domain.repository

import com.rodionov.domain.models.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup

interface OrienteeringCompetitionLocalRepository {

    suspend fun saveCompetition(orienteeringCompetition: OrienteeringCompetition): Result<Any>

    suspend fun saveParticipantsGroups(participantGroups: List<ParticipantGroup>): Result<Any>

}