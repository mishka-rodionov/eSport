package com.competra.clubs.data.join_requests

import com.competra.ui.BaseAction

sealed class ClubJoinRequestsAction : BaseAction {
    data object BackClick : ClubJoinRequestsAction()
    data class Approve(val requestId: String) : ClubJoinRequestsAction()
    data class Reject(val requestId: String) : ClubJoinRequestsAction()
}
