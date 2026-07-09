package com.competra.clubs.data.my_requests

import com.competra.domain.models.club.ClubJoinRequest
import com.competra.ui.BaseState

data class MyJoinRequestsState(
    val requests: List<ClubJoinRequest> = emptyList(),
    val clubNames: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
) : BaseState
