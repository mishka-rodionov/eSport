package com.competra.domain.models.rating

/** Лёгкая карточка рейтинга для глобального поиска — без списка групп, они нужны только в деталях. */
data class RatingSummary(
    val id: String,
    val name: String,
    val ownerClubId: String,
    val ownerClubName: String,
    val createdAt: Long
)
