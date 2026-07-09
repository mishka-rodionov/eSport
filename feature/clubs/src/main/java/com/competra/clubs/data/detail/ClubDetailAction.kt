package com.competra.clubs.data.detail

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.ui.BaseAction

sealed class ClubDetailAction : BaseAction {
    data object BackClick : ClubDetailAction()
    data class SelectTab(val tab: ClubDetailTab) : ClubDetailAction()

    data object JoinClub : ClubDetailAction()
    data object LeaveClub : ClubDetailAction()

    data object OpenEditDialog : ClubDetailAction()
    data object CloseEditDialog : ClubDetailAction()
    data class EditNameChanged(val name: String) : ClubDetailAction()
    data class EditDescriptionChanged(val description: String) : ClubDetailAction()
    data class EditAllowJoinRequestsChanged(val allow: Boolean) : ClubDetailAction()
    data object SaveEdit : ClubDetailAction()

    data object OpenDeleteConfirm : ClubDetailAction()
    data object CloseDeleteConfirm : ClubDetailAction()
    data object ConfirmDelete : ClubDetailAction()

    data object OpenJoinRequests : ClubDetailAction()
    data object OpenRatings : ClubDetailAction()

    data class TeamClick(val teamId: String) : ClubDetailAction()
    data object OpenCreateTeamDialog : ClubDetailAction()
    data object CloseCreateTeamDialog : ClubDetailAction()
    data class CreateTeamNameChanged(val name: String) : ClubDetailAction()
    data class CreateTeamSportChanged(val sport: KindOfSport) : ClubDetailAction()
    data object SaveCreateTeam : ClubDetailAction()

    data class OpenRoleChangeDialog(val member: ClubMember) : ClubDetailAction()
    data object CloseRoleChangeDialog : ClubDetailAction()
    data class ChangeMemberRole(val member: ClubMember, val newRole: ClubRole) : ClubDetailAction()
    data class RemoveMember(val member: ClubMember) : ClubDetailAction()
}
