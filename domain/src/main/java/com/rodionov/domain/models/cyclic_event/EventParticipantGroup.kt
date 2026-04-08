package com.rodionov.domain.models.cyclic_event

import kotlinx.serialization.Serializable

/**
 * Модель группы участников события.
 */
@Serializable
data class EventParticipantGroup(
    val groupId: String,
    val title: String,
    val description: String?,
    val maxParticipant: Int,
    val registeredParticipant: Int
)
