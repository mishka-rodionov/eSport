package com.rodionov.domain.models.cyclic_event

data class CyclicEventDetails(
    val eventId: Long,
    val organizationId: String,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val city: String,
    val participantGroups: List<EventParticipantGroup>
)
