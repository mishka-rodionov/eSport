package com.competra.domain.models.rating

/** Рейтинг соревнований, принадлежащий клубу-владельцу. Online-only, без offline-first. */
data class RatingSeries(
    val id: String,
    val name: String,
    val ownerClubId: String,
    val groups: List<RatingGroup>,
    val createdAt: Long
)
