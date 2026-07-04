package com.competra.eventdetails.di

import com.competra.eventdetails.presentation.details.EventDetailsViewModel
import com.competra.eventdetails.presentation.group_splits.EventGroupSplitsTableViewModel
import com.competra.eventdetails.presentation.live_results.LiveResultsViewModel
import com.competra.eventdetails.presentation.participant_group.EventParticipantGroupViewModel
import com.competra.eventdetails.presentation.results.EventResultsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val eventDetailsModule = module {
    viewModelOf(::EventDetailsViewModel)
    viewModelOf(::EventParticipantGroupViewModel)
    viewModelOf(::EventResultsViewModel)
    viewModelOf(::EventGroupSplitsTableViewModel)
    viewModelOf(::LiveResultsViewModel)
}
