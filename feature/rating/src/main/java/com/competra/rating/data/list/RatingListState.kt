package com.competra.rating.data.list

import com.competra.domain.models.rating.RatingSeries
import com.competra.ui.BaseState

data class RatingListState(
    val clubId: String = "",
    val ratings: List<RatingSeries> = emptyList(),
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false
) : BaseState
