package com.competra.clubs.data.team_detail

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole
import com.competra.ui.BaseAction

sealed class TeamDetailAction : BaseAction {
    data object BackClick : TeamDetailAction()

    data object OpenEditDialog : TeamDetailAction()
    data object CloseEditDialog : TeamDetailAction()
    data class EditNameChanged(val name: String) : TeamDetailAction()
    data class EditSportChanged(val sport: KindOfSport) : TeamDetailAction()
    data object SaveEdit : TeamDetailAction()

    data object OpenDeleteConfirm : TeamDetailAction()
    data object CloseDeleteConfirm : TeamDetailAction()
    data object ConfirmDelete : TeamDetailAction()

    data object OpenAddMemberDialog : TeamDetailAction()
    data object CloseAddMemberDialog : TeamDetailAction()
    data class AddMember(val clubMember: ClubMember) : TeamDetailAction()

    data class OpenRoleChangeDialog(val member: TeamMember) : TeamDetailAction()
    data object CloseRoleChangeDialog : TeamDetailAction()
    data class ChangeMemberRole(val member: TeamMember, val newRole: TeamRole) : TeamDetailAction()
    data class RemoveMember(val member: TeamMember) : TeamDetailAction()
}
