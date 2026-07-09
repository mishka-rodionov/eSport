package com.competra.clubs.data.join_requests

import com.competra.domain.models.club.ClubJoinRequest
import com.competra.ui.BaseState

data class ClubJoinRequestsState(
    val clubId: String = "",
    val requests: List<ClubJoinRequest> = emptyList(),
    val isLoading: Boolean = false
) : BaseState
