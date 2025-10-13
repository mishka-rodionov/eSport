package com.rodionov.domain.models.orienteering

data class OrienteeringCompetitionDetails(
    val competition: OrienteeringCompetition,
    val groupsWithParticipants: List<ParticipantGroupParticipants>
)
