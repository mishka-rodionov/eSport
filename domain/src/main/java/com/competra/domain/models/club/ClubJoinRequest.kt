package com.competra.domain.models.club

/** Заявка на вступление в клуб. Подать можно только если у клуба [Club.allowJoinRequests] == true. */
data class ClubJoinRequest(
    val id: String,
    val clubId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val status: JoinRequestStatus,
    val createdAt: Long
)
