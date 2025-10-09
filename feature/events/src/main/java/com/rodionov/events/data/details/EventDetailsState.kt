package com.rodionov.events.data.details

import com.rodionov.domain.models.Competition
import com.rodionov.ui.BaseState

data class EventDetailsState(
    val competition: Competition?
) : BaseState