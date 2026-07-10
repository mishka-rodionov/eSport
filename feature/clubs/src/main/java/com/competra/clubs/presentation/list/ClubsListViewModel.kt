package com.competra.clubs.presentation.list

import androidx.lifecycle.viewModelScope
import com.competra.clubs.data.list.ClubsListAction
import com.competra.clubs.data.list.ClubsListState
import com.competra.data.navigation.ClubsNavigation
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class ClubsListViewModel(
    private val clubRepository: ClubRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
) : BaseViewModel<ClubsListState>(ClubsListState()) {

    init {
        loadClubs(reset = true)
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is ClubsListAction.QueryChanged -> {
                updateState { copy(query = action.query) }
            }
            is ClubsListAction.Search -> loadClubs(reset = true)
            is ClubsListAction.LoadMore -> {
                if (stateValue.hasMore && !stateValue.isLoading) loadClubs(reset = false)
            }
            is ClubsListAction.ClubClick -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.ClubDetailRoute(action.clubId))
                }
            }
            is ClubsListAction.CreateClubClick -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.CreateClubRoute)
                }
            }
            is ClubsListAction.MyJoinRequestsClick -> {
                viewModelScope.launch {
                    navigation.navigate(ClubsNavigation.MyJoinRequestsRoute)
                }
            }
            is ClubsListAction.RatingsSearchClick -> {
                viewModelScope.launch {
                    navigation.navigate(RatingNavigation.RatingsSearchRoute)
                }
            }
        }
    }

    private fun loadClubs(reset: Boolean) {
        viewModelScope.launch {
            val page = if (reset) 0 else stateValue.page
            updateState { copy(isLoading = true) }
            clubRepository.search(stateValue.query.ifBlank { null }, page, PAGE_SIZE)
                .onSuccess { paged ->
                    updateState {
                        copy(
                            clubs = if (reset) paged.items else clubs + paged.items,
                            hasMore = paged.hasMore,
                            page = page + 1,
                            isLoading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }
}
