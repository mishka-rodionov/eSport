package com.competra.rating.presentation.list

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.club.ClubRole
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.domain.repository.rating.RatingRepository
import com.competra.domain.repository.user.UserRepository
import com.competra.rating.data.list.RatingListAction
import com.competra.rating.data.list.RatingListState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class RatingListViewModel(
    private val ratingRepository: RatingRepository,
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
) : BaseViewModel<RatingListState>(RatingListState()) {

    fun initialize(clubId: String) {
        if (stateValue.clubId == clubId && stateValue.ratings.isNotEmpty()) return
        updateState { copy(clubId = clubId) }
        reload()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is RatingListAction.BackClick -> viewModelScope.launch { navigation.back() }
            is RatingListAction.CreateRatingClick -> {
                viewModelScope.launch {
                    navigation.navigate(RatingNavigation.CreateRatingRoute(stateValue.clubId))
                }
            }
            is RatingListAction.RatingClick -> {
                viewModelScope.launch {
                    navigation.navigate(RatingNavigation.RatingDetailRoute(action.ratingId))
                }
            }
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val clubId = stateValue.clubId
            val userId = userRepository.retrieveUser().getOrNull()?.id

            val ratingsResult = ratingRepository.listForClub(clubId)
            val isAdmin = if (userId != null) {
                clubRepository.getMembers(clubId).getOrDefault(emptyList())
                    .any { it.userId == userId && it.role in listOf(ClubRole.FOUNDER, ClubRole.ADMIN) }
            } else {
                false
            }

            ratingsResult
                .onSuccess { ratings ->
                    updateState { copy(ratings = ratings, isAdmin = isAdmin, isLoading = false) }
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }
}
