package com.competra.events.presentation.main

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.EventsNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.Competition
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.events.EventsFilter
import com.competra.domain.repository.LoadingRepository
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.events.EventsRepository
import com.competra.events.data.main.EVENTS_PAGE_SIZE
import com.competra.events.data.main.EventsAction
import com.competra.events.data.main.EventsPagingSource
import com.competra.events.data.main.EventsState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class EventsViewModel(
    private val navigation: Navigation,
    private val eventsRepository: EventsRepository,
    private val networkErrorRepository: NetworkErrorRepository,
    private val loadingRepository: LoadingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<EventsState>(EventsState()) {

    private val filterFlow = MutableStateFlow(stateValue.appliedFilter)

    @OptIn(ExperimentalCoroutinesApi::class)
    val events: Flow<PagingData<Competition>> = filterFlow
        .flatMapLatest { filter ->
            Pager(
                config = PagingConfig(pageSize = EVENTS_PAGE_SIZE, initialLoadSize = EVENTS_PAGE_SIZE),
                pagingSourceFactory = {
                    EventsPagingSource(eventsRepository, filter, loadingRepository, ::handleFailure)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    override fun onAction(action: BaseAction) {

    }

    fun onAction(action: EventsAction) {
        when (action) {
            is EventsAction.EventClick -> {
                val id = action.eventId ?: return
                analytics.trackEvent(
                    AnalyticsEvent.EventOpened(
                        eventId = id,
                        source = AnalyticsEvent.EventSource.LIST,
                    )
                )
                viewModelScope.launch {
                    navigation.navigate(
                        EventsNavigation.EventDetailsRoute(eventId = id)
                    )
                }
            }
            is EventsAction.OpenFilterDialog -> {
                updateState { copy(isFilterDialogOpen = true) }
            }
            is EventsAction.CloseFilterDialog -> {
                updateState { copy(isFilterDialogOpen = false) }
            }
            is EventsAction.ApplyFilter -> {
                analytics.trackEvent(AnalyticsEvent.EventFilterApplied(action.filter.toAnalyticsParams()))
                updateState { copy(appliedFilter = action.filter, isFilterDialogOpen = false) }
                filterFlow.value = action.filter
            }
            is EventsAction.ResetFilter -> {
                val cleared = EventsFilter()
                updateState { copy(appliedFilter = cleared) }
                filterFlow.value = cleared
            }
        }
    }

    private fun EventsFilter.toAnalyticsParams(): Map<String, Any?> = mapOf(
        "kinds_count" to kindOfSports.size,
        "statuses_count" to statuses.size,
        "has_date_from" to (dateFrom != null),
        "has_date_to" to (dateTo != null),
        "include_test" to includeTest,
    )

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }
}

@Parcelize
data class DetailsInfo(
    val title: String,
    val description: String
) : Parcelable
