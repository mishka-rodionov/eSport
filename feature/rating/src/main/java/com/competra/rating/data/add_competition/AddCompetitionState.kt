package com.competra.rating.data.add_competition

import com.competra.domain.models.Competition
import com.competra.ui.BaseState

data class AddCompetitionState(
    val ratingId: String = "",
    val query: String = "",
    val competitions: List<Competition> = emptyList(),
    val alreadyAddedCompetitionIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isAdding: Boolean = false
) : BaseState
