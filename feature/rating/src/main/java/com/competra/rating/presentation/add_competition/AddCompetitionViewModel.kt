package com.competra.rating.presentation.add_competition

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.events.EventsFilter
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.events.EventsRepository
import com.competra.domain.repository.rating.RatingRepository
import com.competra.rating.data.add_competition.AddCompetitionAction
import com.competra.rating.data.add_competition.AddCompetitionState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class AddCompetitionViewModel(
    private val ratingRepository: RatingRepository,
    private val eventsRepository: EventsRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<AddCompetitionState>(AddCompetitionState()) {

    fun initialize(ratingId: String) {
        if (stateValue.ratingId == ratingId) return
        updateState { copy(ratingId = ratingId) }
        search()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is AddCompetitionAction.BackClick -> viewModelScope.launch { navigation.back() }
            is AddCompetitionAction.QueryChanged -> updateState { copy(query = action.query) }
            is AddCompetitionAction.Search -> search()
            is AddCompetitionAction.CompetitionClick -> addCompetition(action.competitionId)
        }
    }

    private fun search() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            eventsRepository.getEvents(EventsFilter(searchQuery = stateValue.query.trim().ifBlank { null }), page = 0, limit = PAGE_SIZE)
                .onSuccess { paged ->
                    updateState { copy(competitions = paged.items, isLoading = false) }
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false) }
                    emitNetworkError(throwable)
                }
        }
    }

    private fun addCompetition(competitionId: String) {
        viewModelScope.launch {
            updateState { copy(isAdding = true) }
            ratingRepository.addCompetition(stateValue.ratingId, competitionId)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.RatingCompetitionAdded(stateValue.ratingId, competitionId))
                    updateState { copy(isAdding = false) }
                    navigation.navigate(RatingNavigation.GroupMappingRoute(stateValue.ratingId, competitionId))
                }
                .onFailure { throwable ->
                    updateState { copy(isAdding = false) }
                    emitNetworkError(throwable)
                }
        }
    }

    private suspend fun emitNetworkError(throwable: Throwable) {
        val code = (throwable as? NetworkException)?.code
        networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
    }
}
