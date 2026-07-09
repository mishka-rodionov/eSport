package com.competra.clubs.data.team_detail

import com.competra.domain.models.KindOfSport
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.Team
import com.competra.domain.models.club.TeamMember
import com.competra.ui.BaseState

data class TeamDetailState(
    val teamId: String = "",
    val team: Team? = null,
    val members: List<TeamMember> = emptyList(),
    val clubMembers: List<ClubMember> = emptyList(),
    val myUserId: String? = null,
    val isClubAdmin: Boolean = false,
    val isLoading: Boolean = false,
    val isEditDialogOpen: Boolean = false,
    val editName: String = "",
    val editSportType: KindOfSport = KindOfSport.Orienteering,
    val isEditSaving: Boolean = false,
    val isDeleteConfirmOpen: Boolean = false,
    val isAddMemberDialogOpen: Boolean = false,
    val roleChangeTarget: TeamMember? = null
) : BaseState {
    /** Участники клуба, которых ещё нет в команде — кандидаты для добавления. */
    val availableClubMembers: List<ClubMember>
        get() = clubMembers.filter { cm -> members.none { it.clubMemberId == cm.id } }
}
