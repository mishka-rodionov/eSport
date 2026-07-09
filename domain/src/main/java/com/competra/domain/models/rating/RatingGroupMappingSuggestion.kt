package com.competra.domain.models.rating

/** Авто-подсказка соответствия группы соревнования канонической группе рейтинга — требует подтверждения организатором. */
data class RatingGroupMappingSuggestion(
    val participantGroupId: Long,
    val participantGroupTitle: String,
    val suggestedRatingGroupId: Long?,
    val confidence: Float
)

data class AddCompetitionToRatingResult(
    val ratingCompetition: RatingCompetition,
    val groupMappingSuggestions: List<RatingGroupMappingSuggestion>
)
