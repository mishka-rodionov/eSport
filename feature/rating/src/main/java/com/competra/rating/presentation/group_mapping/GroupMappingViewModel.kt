package com.competra.rating.presentation.group_mapping

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.rating.RatingRepository
import com.competra.rating.data.group_mapping.GroupMappingAction
import com.competra.rating.data.group_mapping.GroupMappingState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class GroupMappingViewModel(
    private val ratingRepository: RatingRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<GroupMappingState>(GroupMappingState()) {

    fun initialize(ratingId: String, competitionId: String) {
        if (stateValue.ratingId == ratingId && stateValue.competitionId == competitionId && stateValue.suggestions.isNotEmpty()) return
        updateState { copy(ratingId = ratingId, competitionId = competitionId) }
        reload()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is GroupMappingAction.BackClick -> viewModelScope.launch { navigation.back() }
            is GroupMappingAction.MappingChanged -> {
                updateState { copy(mapping = mapping + (action.participantGroupId to action.ratingGroupId)) }
            }
            is GroupMappingAction.Save -> save()
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val ratingResult = ratingRepository.getById(stateValue.ratingId)
            val suggestionsResult = ratingRepository.getGroupMappingSuggestions(stateValue.ratingId, stateValue.competitionId)

            val failure = ratingResult.exceptionOrNull() ?: suggestionsResult.exceptionOrNull()
            if (failure != null) {
                updateState { copy(isLoading = false) }
                emitNetworkError(failure)
                return@launch
            }

            val suggestions = suggestionsResult.getOrDefault(emptyList())
            val mapping = suggestions.associate { it.participantGroupId to it.suggestedRatingGroupId }

            updateState {
                copy(
                    ratingGroups = ratingResult.getOrNull()?.groups.orEmpty(),
                    suggestions = suggestions,
                    mapping = mapping,
                    isLoading = false
                )
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            val mappings = stateValue.mapping.filterValues { it != null }.mapValues { it.value!! }
            ratingRepository.setGroupMapping(stateValue.ratingId, stateValue.competitionId, mappings)
                .onSuccess {
                    analytics.trackEvent(
                        AnalyticsEvent.RatingGroupMappingConfirmed(stateValue.ratingId, stateValue.competitionId)
                    )
                    updateState { copy(isSaving = false) }
                    navigation.navigate(RatingNavigation.RatingDetailRoute(stateValue.ratingId))
                }
                .onFailure { throwable ->
                    updateState { copy(isSaving = false) }
                    emitNetworkError(throwable)
                }
        }
    }

    private suspend fun emitNetworkError(throwable: Throwable) {
        val code = (throwable as? NetworkException)?.code
        networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
    }
}
