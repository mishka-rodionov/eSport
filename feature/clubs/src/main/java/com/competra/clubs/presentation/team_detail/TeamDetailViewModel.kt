package com.competra.clubs.presentation.team_detail

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.clubs.data.team_detail.TeamDetailAction
import com.competra.clubs.data.team_detail.TeamDetailState
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.models.club.TeamMember
import com.competra.domain.models.club.TeamRole
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.domain.repository.clubs.TeamRepository
import com.competra.domain.repository.user.UserRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    private val teamRepository: TeamRepository,
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<TeamDetailState>(TeamDetailState()) {

    fun initialize(teamId: String) {
        if (stateValue.teamId == teamId && stateValue.team != null) return
        updateState { copy(teamId = teamId) }
        viewModelScope.launch {
            val userId = userRepository.retrieveUser().getOrNull()?.id
            updateState { copy(myUserId = userId) }
            reload()
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is TeamDetailAction.BackClick -> viewModelScope.launch { navigation.back() }
            is TeamDetailAction.OpenEditDialog -> {
                val team = stateValue.team ?: return
                updateState { copy(isEditDialogOpen = true, editName = team.name, editSportType = team.sportType) }
            }
            is TeamDetailAction.CloseEditDialog -> updateState { copy(isEditDialogOpen = false) }
            is TeamDetailAction.EditNameChanged -> updateState { copy(editName = action.name) }
            is TeamDetailAction.EditSportChanged -> updateState { copy(editSportType = action.sport) }
            is TeamDetailAction.SaveEdit -> saveEdit()
            is TeamDetailAction.OpenDeleteConfirm -> updateState { copy(isDeleteConfirmOpen = true) }
            is TeamDetailAction.CloseDeleteConfirm -> updateState { copy(isDeleteConfirmOpen = false) }
            is TeamDetailAction.ConfirmDelete -> confirmDelete()
            is TeamDetailAction.OpenAddMemberDialog -> updateState { copy(isAddMemberDialogOpen = true) }
            is TeamDetailAction.CloseAddMemberDialog -> updateState { copy(isAddMemberDialogOpen = false) }
            is TeamDetailAction.AddMember -> addMember(action.clubMember)
            is TeamDetailAction.OpenRoleChangeDialog -> updateState { copy(roleChangeTarget = action.member) }
            is TeamDetailAction.CloseRoleChangeDialog -> updateState { copy(roleChangeTarget = null) }
            is TeamDetailAction.ChangeMemberRole -> changeMemberRole(action.member, action.newRole)
            is TeamDetailAction.RemoveMember -> removeMember(action.member)
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val teamId = stateValue.teamId

            val teamResult = teamRepository.getById(teamId)
            val team = teamResult.getOrNull()
            if (team == null) {
                updateState { copy(isLoading = false) }
                emitNetworkError(teamResult.exceptionOrNull())
                return@launch
            }

            val membersResult = teamRepository.getMembers(teamId)
            val clubMembersResult = clubRepository.getMembers(team.clubId)

            val failure = membersResult.exceptionOrNull() ?: clubMembersResult.exceptionOrNull()
            if (failure != null) {
                updateState { copy(isLoading = false) }
                emitNetworkError(failure)
                return@launch
            }

            val clubMembers = clubMembersResult.getOrDefault(emptyList())
            val isAdmin = clubMembers.any {
                it.userId == stateValue.myUserId && it.role in listOf(ClubRole.FOUNDER, ClubRole.ADMIN)
            }

            updateState {
                copy(
                    team = team,
                    members = membersResult.getOrDefault(emptyList()),
                    clubMembers = clubMembers,
                    isClubAdmin = isAdmin,
                    isLoading = false
                )
            }
        }
    }

    private fun saveEdit() {
        val team = stateValue.team ?: return
        if (stateValue.editName.isBlank()) return
        viewModelScope.launch {
            updateState { copy(isEditSaving = true) }
            teamRepository.update(team.id, stateValue.editName.trim(), stateValue.editSportType)
                .onSuccess { updated ->
                    updateState { copy(team = updated, isEditSaving = false, isEditDialogOpen = false) }
                }
                .onFailure {
                    updateState { copy(isEditSaving = false) }
                    emitNetworkError(it)
                }
        }
    }

    private fun confirmDelete() {
        val teamId = stateValue.teamId
        viewModelScope.launch {
            teamRepository.delete(teamId)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubTeamDeleted(teamId))
                    updateState { copy(isDeleteConfirmOpen = false) }
                    navigation.back()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun addMember(clubMember: ClubMember) {
        viewModelScope.launch {
            teamRepository.addMember(stateValue.teamId, clubMember.id, TeamRole.MEMBER)
                .onSuccess { member ->
                    updateState { copy(members = members + member, isAddMemberDialogOpen = false) }
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun changeMemberRole(member: TeamMember, newRole: TeamRole) {
        viewModelScope.launch {
            teamRepository.changeMemberRole(stateValue.teamId, member.clubMemberId, newRole)
                .onSuccess {
                    updateState { copy(roleChangeTarget = null) }
                    reload()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun removeMember(member: TeamMember) {
        viewModelScope.launch {
            teamRepository.removeMember(stateValue.teamId, member.clubMemberId)
                .onSuccess {
                    updateState { copy(roleChangeTarget = null, members = members.filterNot { it.id == member.id }) }
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private suspend fun emitNetworkError(throwable: Throwable?) {
        throwable ?: return
        val code = (throwable as? NetworkException)?.code
        networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
    }
}
