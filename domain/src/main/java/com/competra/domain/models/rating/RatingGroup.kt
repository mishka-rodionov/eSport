package com.competra.domain.models.rating

import com.competra.domain.models.Gender

/** Канонический список групп рейтинга — на них мапятся группы конкретных соревнований. */
data class RatingGroup(
    val id: Long,
    val ratingId: String,
    val title: String,
    val gender: Gender?,
    val minAge: Int?,
    val maxAge: Int?,
    val orderIndex: Int
)
