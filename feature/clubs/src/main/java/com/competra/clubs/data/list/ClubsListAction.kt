package com.competra.clubs.data.list

import com.competra.ui.BaseAction

sealed class ClubsListAction : BaseAction {
    data class QueryChanged(val query: String) : ClubsListAction()
    data object Search : ClubsListAction()
    data object LoadMore : ClubsListAction()
    data class ClubClick(val clubId: String) : ClubsListAction()
    data object CreateClubClick : ClubsListAction()
    data object MyJoinRequestsClick : ClubsListAction()
}
