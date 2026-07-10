package com.competra.rating.data.search

import com.competra.domain.models.rating.RatingSummary
import com.competra.ui.BaseState

data class RatingsSearchState(
    val ratings: List<RatingSummary> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val page: Int = 0
) : BaseState
