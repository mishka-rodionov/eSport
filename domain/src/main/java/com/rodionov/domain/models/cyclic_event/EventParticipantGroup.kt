package com.rodionov.domain.models.cyclic_event

data class EventParticipantGroup(
    val groupId: Long,
    val title: String,
    val description: String?,
    val maxParticipant: Int,
    val registeredParticipant: Int
)
