package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Competition

/**
 * Представляет модель соревнований по спортивному ориентированию
 */
data class OrienteeringCompetition(
    val competitionId: Long,
    val competition: Competition,
    val direction: OrienteeringDirection,
    val punchingSystem: PunchingSystem
)
