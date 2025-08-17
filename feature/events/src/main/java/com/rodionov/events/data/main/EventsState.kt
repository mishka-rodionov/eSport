package com.rodionov.events.data.main

import com.rodionov.domain.models.Competition

data class EventsState(
    val events: List<Competition> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorLoadingPage: Boolean = false,
    val isGlobalError: Boolean = false
)
