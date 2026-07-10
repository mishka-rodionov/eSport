package com.competra.rating.data.detail

import com.competra.domain.models.rating.RatingCompetition
import com.competra.domain.models.rating.RatingSeries
import com.competra.domain.models.rating.RatingStanding
import com.competra.ui.BaseState

data class RatingDetailState(
    val ratingId: String = "",
    val rating: RatingSeries? = null,
    val selectedGroupId: Long? = null,
    val standings: List<RatingStanding> = emptyList(),
    val competitions: List<RatingCompetition> = emptyList(),
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false,
    val isStandingsLoading: Boolean = false,
    val isDeleteConfirmOpen: Boolean = false,
    val isDeleting: Boolean = false
) : BaseState
