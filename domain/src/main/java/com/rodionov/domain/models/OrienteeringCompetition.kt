package com.rodionov.domain.models

data class OrienteeringCompetition(
    val id: Long,
    val competition: Competition,
    val direction: OrienteeringDirection
)
