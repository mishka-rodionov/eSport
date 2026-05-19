package com.competra.events.data.main

import com.competra.domain.models.Competition
import com.competra.domain.models.events.EventsFilter
import com.competra.ui.BaseState

data class EventsState(
    val events: List<Competition> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorLoadingPage: Boolean = false,
    val isGlobalError: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val appliedFilter: EventsFilter = EventsFilter()
) : BaseState
