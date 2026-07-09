package com.competra.domain.models.club

/** Членство участника клуба ([ClubMember]) в конкретной команде. */
data class TeamMember(
    val id: String,
    val teamId: String,
    val clubMemberId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val role: TeamRole
)
