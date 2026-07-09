package com.competra.domain.models.rating

data class RatingStandingBreakdownEntry(
    val competitionId: String,
    val place: Int?,
    val points: Int
)

/** Строка итоговой таблицы рейтинга — участник, суммарные очки и разбивка по стартам. */
data class RatingStanding(
    val participantKey: String,
    val displayName: String,
    val totalPoints: Int,
    val rank: Int,
    val breakdown: List<RatingStandingBreakdownEntry>
)
