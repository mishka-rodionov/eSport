package com.competra.clubs.presentation.my_requests

import androidx.lifecycle.viewModelScope
import com.competra.clubs.data.my_requests.MyJoinRequestsAction
import com.competra.clubs.data.my_requests.MyJoinRequestsState
import com.competra.data.navigation.ClubsNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubJoinRequestRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class MyJoinRequestsViewModel(
    private val joinRequestRepository: ClubJoinRequestRepository,
    private val clubRepository: ClubRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
) : BaseViewModel<MyJoinRequestsState>(MyJoinRequestsState()) {

    init {
        reload()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is MyJoinRequestsAction.BackClick -> viewModelScope.launch { navigation.back() }
            is MyJoinRequestsAction.RequestClick -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.ClubDetailRoute(action.clubId))
                }
            }
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            joinRequestRepository.listMine()
                .onSuccess { requests ->
                    updateState { copy(requests = requests, isLoading = false) }
                    loadClubNames(requests.map { it.clubId }.distinct())
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }

    private fun loadClubNames(clubIds: List<String>) {
        viewModelScope.launch {
            val names = clubIds.mapNotNull { id ->
                clubRepository.getById(id).getOrNull()?.let { id to it.name }
            }.toMap()
            updateState { copy(clubNames = names) }
        }
    }
}
