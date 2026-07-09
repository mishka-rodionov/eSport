package com.competra.clubs.presentation.join_requests

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.clubs.data.join_requests.ClubJoinRequestsAction
import com.competra.clubs.data.join_requests.ClubJoinRequestsState
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubJoinRequestRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class ClubJoinRequestsViewModel(
    private val joinRequestRepository: ClubJoinRequestRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<ClubJoinRequestsState>(ClubJoinRequestsState()) {

    fun initialize(clubId: String) {
        if (stateValue.clubId == clubId) return
        updateState { copy(clubId = clubId) }
        reload()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is ClubJoinRequestsAction.BackClick -> viewModelScope.launch { navigation.back() }
            is ClubJoinRequestsAction.Approve -> review(action.requestId, approve = true)
            is ClubJoinRequestsAction.Reject -> review(action.requestId, approve = false)
        }
    }

    private fun reload() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            joinRequestRepository.listForClub(stateValue.clubId)
                .onSuccess { requests -> updateState { copy(requests = requests, isLoading = false) } }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }

    private fun review(requestId: String, approve: Boolean) {
        viewModelScope.launch {
            joinRequestRepository.review(stateValue.clubId, requestId, approve)
                .onSuccess {
                    analytics.trackEvent(AnalyticsEvent.ClubJoinRequestReviewed(stateValue.clubId, approve))
                    updateState { copy(requests = requests.filterNot { it.id == requestId }) }
                }
                .onFailure { throwable ->
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }
}
