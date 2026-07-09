package com.competra.clubs.data.my_requests

import com.competra.ui.BaseAction

sealed class MyJoinRequestsAction : BaseAction {
    data object BackClick : MyJoinRequestsAction()
    data class RequestClick(val clubId: String) : MyJoinRequestsAction()
}
