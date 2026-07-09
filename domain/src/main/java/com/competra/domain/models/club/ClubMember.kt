package com.competra.domain.models.club

/** Членство зарегистрированного пользователя в клубе. Гостевые/анонимные участники не допускаются. */
data class ClubMember(
    val id: String,
    val clubId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val role: ClubRole,
    val joinedAt: Long
)
