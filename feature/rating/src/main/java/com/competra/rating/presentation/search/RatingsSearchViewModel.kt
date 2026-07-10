package com.competra.rating.presentation.search

import androidx.lifecycle.viewModelScope
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.rating.RatingRepository
import com.competra.rating.data.search.RatingsSearchAction
import com.competra.rating.data.search.RatingsSearchState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class RatingsSearchViewModel(
    private val ratingRepository: RatingRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
) : BaseViewModel<RatingsSearchState>(RatingsSearchState()) {

    init {
        loadRatings(reset = true)
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is RatingsSearchAction.BackClick -> viewModelScope.launch { navigation.back() }
            is RatingsSearchAction.QueryChanged -> {
                updateState { copy(query = action.query) }
            }
            is RatingsSearchAction.Search -> loadRatings(reset = true)
            is RatingsSearchAction.LoadMore -> {
                if (stateValue.hasMore && !stateValue.isLoading) loadRatings(reset = false)
            }
            is RatingsSearchAction.RatingClick -> {
                viewModelScope.launch {
                    navigation.navigate(RatingNavigation.RatingDetailRoute(action.ratingId))
                }
            }
        }
    }

    private fun loadRatings(reset: Boolean) {
        viewModelScope.launch {
            val page = if (reset) 0 else stateValue.page
            updateState { copy(isLoading = true) }
            ratingRepository.search(stateValue.query.ifBlank { null }, page, PAGE_SIZE)
                .onSuccess { paged ->
                    updateState {
                        copy(
                            ratings = if (reset) paged.items else ratings + paged.items,
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
