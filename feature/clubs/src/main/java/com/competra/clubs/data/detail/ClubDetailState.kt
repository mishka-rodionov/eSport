package com.competra.clubs.data.detail

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.Club
import com.competra.domain.models.club.ClubJoinRequest
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.models.club.Team
import com.competra.ui.BaseState

enum class ClubDetailTab { MEMBERS, TEAMS }

data class ClubDetailState(
    val clubId: String = "",
    val club: Club? = null,
    val members: List<ClubMember> = emptyList(),
    val teams: List<Team> = emptyList(),
    val myUserId: String? = null,
    val myPendingJoinRequest: ClubJoinRequest? = null,
    val isLoading: Boolean = false,
    val selectedTab: ClubDetailTab = ClubDetailTab.MEMBERS,
    val isEditDialogOpen: Boolean = false,
    val editName: String = "",
    val editDescription: String = "",
    val editAllowJoinRequests: Boolean = true,
    val isEditSaving: Boolean = false,
    val isDeleteConfirmOpen: Boolean = false,
    val isCreateTeamDialogOpen: Boolean = false,
    val createTeamName: String = "",
    val createTeamSportType: KindOfSport = KindOfSport.Orienteering,
    val isCreateTeamSaving: Boolean = false,
    val roleChangeTarget: ClubMember? = null
) : BaseState {

    val myMembership: ClubMember? get() = members.firstOrNull { it.userId == myUserId }
    val isAdmin: Boolean get() = myMembership?.role in listOf(ClubRole.FOUNDER, ClubRole.ADMIN)
    val isFounder: Boolean get() = myMembership?.role == ClubRole.FOUNDER
}
