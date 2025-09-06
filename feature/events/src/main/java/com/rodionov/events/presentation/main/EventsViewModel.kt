package com.rodionov.events.presentation.main

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseArgument
import com.rodionov.data.navigation.EventsNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.repository.events.EventsRepository
import com.rodionov.events.data.main.EventsAction
import com.rodionov.events.data.main.EventsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class EventsViewModel(
    private val navigation: Navigation,
    private val eventsRepository: EventsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventsState())
    val state: StateFlow<EventsState> = _state.asStateFlow()

    fun onAction(action: EventsAction) {
        when (action) {
            is EventsAction.EventClick -> {
                viewModelScope.launch {
                    navigation.navigate(
                        EventsNavigation.EventDetailsRoute, argument =
                            navigation.createArguments("temp" to _state.value.events[action.eventId])
                    )
                }
            }
        }
    }

    fun getEvents(kindOfSports: List<KindOfSport> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.getEvents(kindOfSport = kindOfSports).onSuccess { events ->
                events?.also { list ->
                    _state.update { it.copy(events = list) }
                }
            }.onFailure {
                _state.update { it.copy(isGlobalError = true) }
            }
        }
    }
}

@Parcelize
data class DetailsInfo(
    val title: String,
    val description: String
) : Parcelable