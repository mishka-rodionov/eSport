package com.competra.events.data.main

import com.competra.domain.models.events.EventsFilter
import com.competra.ui.BaseState

data class EventsState(
    val isFilterDialogOpen: Boolean = false,
    val appliedFilter: EventsFilter = EventsFilter()
) : BaseState
