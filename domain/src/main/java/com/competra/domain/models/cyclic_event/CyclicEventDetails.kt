package com.competra.domain.models.cyclic_event

import com.competra.domain.models.events.EventStatus
import com.competra.domain.models.events.EventType

data class CyclicEventDetails(
    val eventId: Long,
    val organizationId: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val endRegistrationDate: Long,
    val maxParticipants: Int,
    val city: String,
    val participantGroups: List<EventParticipantGroup>,
    val status: EventStatus,
    val eventType: EventType,
    val isUserRegistered: Boolean = false,
    val imageUrl: String? = null
)
