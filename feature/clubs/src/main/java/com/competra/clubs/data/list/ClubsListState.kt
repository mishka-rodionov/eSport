package com.competra.clubs.data.list

import com.competra.domain.models.club.Club
import com.competra.ui.BaseState

data class ClubsListState(
    val clubs: List<Club> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val page: Int = 0
) : BaseState
