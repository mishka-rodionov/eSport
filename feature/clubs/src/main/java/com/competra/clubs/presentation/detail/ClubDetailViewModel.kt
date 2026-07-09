package com.competra.clubs.presentation.detail

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.clubs.data.detail.ClubDetailAction
import com.competra.clubs.data.detail.ClubDetailState
import com.competra.clubs.data.detail.ClubDetailTab
import com.competra.data.navigation.ClubsNavigation
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.club.ClubMember
import com.competra.domain.models.club.ClubRole
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubJoinRequestRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.domain.repository.clubs.TeamRepository
import com.competra.domain.repository.user.UserRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class ClubDetailViewModel(
    private val clubRepository: ClubRepository,
    private val teamRepository: TeamRepository,
    private val joinRequestRepository: ClubJoinRequestRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<ClubDetailState>(ClubDetailState()) {

    fun initialize(clubId: String) {
        if (stateValue.clubId == clubId && stateValue.club != null) return
        updateState { copy(clubId = clubId) }
        viewModelScope.launch {
            val userId = userRepository.retrieveUser().getOrNull()?.id
            updateState { copy(myUserId = userId) }
            reload()
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is ClubDetailAction.BackClick -> viewModelScope.launch { navigation.back() }
            is ClubDetailAction.SelectTab -> updateState { copy(selectedTab = action.tab) }
            is ClubDetailAction.JoinClub -> joinClub()
            is ClubDetailAction.LeaveClub -> leaveClub()
            is ClubDetailAction.OpenEditDialog -> {
                val club = stateValue.club ?: return
                updateState {
                    copy(
                        isEditDialogOpen = true,
                        editName = club.name,
                        editDescription = club.description.orEmpty(),
                        editAllowJoinRequests = club.allowJoinRequests
                    )
                }
            }
            is ClubDetailAction.CloseEditDialog -> updateState { copy(isEditDialogOpen = false) }
            is ClubDetailAction.EditNameChanged -> updateState { copy(editName = action.name) }
            is ClubDetailAction.EditDescriptionChanged -> updateState { copy(editDescription = action.description) }
            is ClubDetailAction.EditAllowJoinRequestsChanged -> {
                updateState { copy(editAllowJoinRequests = action.allow) }
            }
            is ClubDetailAction.SaveEdit -> saveEdit()
            is ClubDetailAction.OpenDeleteConfirm -> updateState { copy(isDeleteConfirmOpen = true) }
            is ClubDetailAction.CloseDeleteConfirm -> updateState { copy(isDeleteConfirmOpen = false) }
            is ClubDetailAction.ConfirmDelete -> confirmDelete()
            is ClubDetailAction.OpenJoinRequests -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.ClubJoinRequestsRoute(stateValue.clubId))
                }
            }
            is ClubDetailAction.OpenRatings -> {
                viewModelScope.launch {
                    navigation.navigate(RatingNavigation.RatingListRoute(stateValue.clubId))
                }
            }
            is ClubDetailAction.TeamClick -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.TeamDetailRoute(action.teamId))
                }
            }
            is ClubDetailAction.OpenCreateTeamDialog -> {
                updateState {
                    copy(
                        isCreateTeamDialogOpen = true,
                        createTeamName = "",
                        createTeamSportType = com.competra.domain.models.KindOfSport.Orienteering
                    )
                }
            }
            is ClubDetailAction.CloseCreateTeamDialog -> updateState { copy(isCreateTeamDialogOpen = false) }
            is ClubDetailAction.CreateTeamNameChanged -> updateState { copy(createTeamName = action.name) }
            is ClubDetailAction.CreateTeamSportChanged -> updateState { copy(createTeamSportType = action.sport) }
            is ClubDetailAction.SaveCreateTeam -> saveCreateTeam()
            is ClubDetailAction.OpenRoleChangeDialog -> updateState { copy(roleChangeTarget = action.member) }
            is ClubDetailAction.CloseRoleChangeDialog -> updateState { copy(roleChangeTarget = null) }
            is ClubDetailAction.ChangeMemberRole -> changeMemberRole(action.member, action.newRole)
            is ClubDetailAction.RemoveMember -> removeMember(action.member)
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val clubId = stateValue.clubId

            val clubResult = clubRepository.getById(clubId)
            val membersResult = clubRepository.getMembers(clubId)
            val teamsResult = teamRepository.listByClub(clubId)

            val failure = clubResult.exceptionOrNull() ?: membersResult.exceptionOrNull() ?: teamsResult.exceptionOrNull()
            if (failure != null) {
                updateState { copy(isLoading = false) }
                val code = (failure as? NetworkException)?.code
                networkErrorRepository.emit(NetworkErrorEvent(code = code, message = failure.message))
                return@launch
            }

            val members = membersResult.getOrDefault(emptyList())
            val isMember = members.any { it.userId == stateValue.myUserId }
            val myPendingRequest = if (!isMember) {
                joinRequestRepository.listMine().getOrDefault(emptyList())
                    .filter { it.clubId == clubId }
                    .maxByOrNull { it.createdAt }
            } else {
                null
            }

            updateState {
                copy(
                    club = clubResult.getOrNull(),
                    members = members,
                    teams = teamsResult.getOrDefault(emptyList()),
                    myPendingJoinRequest = myPendingRequest,
                    isLoading = false
                )
            }
        }
    }

    private fun joinClub() {
        viewModelScope.launch {
            joinRequestRepository.create(stateValue.clubId)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubJoinRequested(stateValue.clubId))
                    updateState { copy(myPendingJoinRequest = it) }
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun leaveClub() {
        val myUserId = stateValue.myUserId ?: return
        viewModelScope.launch {
            clubRepository.removeMember(stateValue.clubId, myUserId)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubMemberRemoved(stateValue.clubId, isSelf = true))
                    reload()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun saveEdit() {
        val club = stateValue.club ?: return
        viewModelScope.launch {
            updateState { copy(isEditSaving = true) }
            clubRepository.update(
                clubId = club.id,
                name = stateValue.editName.trim(),
                description = stateValue.editDescription.trim().ifBlank { null },
                allowJoinRequests = stateValue.editAllowJoinRequests
            )
                .onSuccess { updated ->
                    analytics.trackEvent(AnalyticsEvent.ClubUpdated(updated.id))
                    updateState { copy(club = updated, isEditSaving = false, isEditDialogOpen = false) }
                }
                .onFailure {
                    updateState { copy(isEditSaving = false) }
                    emitNetworkError(it)
                }
        }
    }

    private fun confirmDelete() {
        val clubId = stateValue.clubId
        viewModelScope.launch {
            clubRepository.delete(clubId)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubDeleted(clubId))
                    updateState { copy(isDeleteConfirmOpen = false) }
                    navigation.back()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun saveCreateTeam() {
        if (stateValue.createTeamName.isBlank()) return
        viewModelScope.launch {
            updateState { copy(isCreateTeamSaving = true) }
            teamRepository.create(stateValue.clubId, stateValue.createTeamName.trim(), stateValue.createTeamSportType)
                .onSuccess { team ->
                    analytics.trackEvent(AnalyticsEvent.ClubTeamCreated(stateValue.clubId))
                    updateState {
                        copy(teams = teams + team, isCreateTeamSaving = false, isCreateTeamDialogOpen = false)
                    }
                }
                .onFailure {
                    updateState { copy(isCreateTeamSaving = false) }
                    emitNetworkError(it)
                }
        }
    }

    private fun changeMemberRole(member: ClubMember, newRole: ClubRole) {
        viewModelScope.launch {
            clubRepository.changeMemberRole(stateValue.clubId, member.userId, newRole)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubMemberRoleChanged(stateValue.clubId, newRole.name))
                    updateState { copy(roleChangeTarget = null) }
                    reload()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private fun removeMember(member: ClubMember) {
        viewModelScope.launch {
            clubRepository.removeMember(stateValue.clubId, member.userId)
                .onSuccess {
                    analytics.trackEvent(
                        AnalyticsEvent.ClubMemberRemoved(stateValue.clubId, isSelf = member.userId == stateValue.myUserId)
                    )
                    reload()
                }
                .onFailure { emitNetworkError(it) }
        }
    }

    private suspend fun emitNetworkError(throwable: Throwable) {
        val code = (throwable as? NetworkException)?.code
        networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
    }
}
