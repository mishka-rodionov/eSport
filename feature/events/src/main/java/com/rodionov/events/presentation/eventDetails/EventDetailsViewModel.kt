package com.rodionov.events.presentation.eventDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.Competition
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.events.presentation.main.DetailsInfo
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventDetailsViewModel(
    val savedStateHandle: SavedStateHandle,
    private val navigation: Navigation
) : ViewModel() {

    private val _state = MutableStateFlow(
        EventDetailsState(
            competition = navigation.getArguments<Competition>(EventsConstants.EVENT_ID.name)
        )
    )
    val state: StateFlow<EventDetailsState> = _state.asStateFlow()

}