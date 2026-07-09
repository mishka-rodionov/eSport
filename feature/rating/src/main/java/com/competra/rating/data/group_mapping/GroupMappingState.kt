package com.competra.rating.data.group_mapping

import com.competra.domain.models.rating.RatingGroup
import com.competra.domain.models.rating.RatingGroupMappingSuggestion
import com.competra.ui.BaseState

data class GroupMappingState(
    val ratingId: String = "",
    val competitionId: String = "",
    val ratingGroups: List<RatingGroup> = emptyList(),
    val suggestions: List<RatingGroupMappingSuggestion> = emptyList(),
    val mapping: Map<Long, Long?> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
) : BaseState
