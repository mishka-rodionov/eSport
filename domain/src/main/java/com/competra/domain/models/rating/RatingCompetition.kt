package com.competra.domain.models.rating

/** Соревнование, включённое в состав рейтинга. */
data class RatingCompetition(
    val id: String,
    val ratingId: String,
    val competitionId: String,
    val competitionTitle: String,
    val competitionStartDate: Long,
    val addedAt: Long
)
