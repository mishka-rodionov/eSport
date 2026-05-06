package com.rodionov.eventdetails.di

import com.rodionov.eventdetails.presentation.details.EventDetailsViewModel
import com.rodionov.eventdetails.presentation.live_results.LiveResultsViewModel
import com.rodionov.eventdetails.presentation.participant_group.EventParticipantGroupViewModel
import com.rodionov.eventdetails.presentation.results.EventResultsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val eventDetailsModule = module {
    viewModelOf(::EventDetailsViewModel)
    viewModelOf(::EventParticipantGroupViewModel)
    viewModelOf(::EventResultsViewModel)
    viewModelOf(::LiveResultsViewModel)
}
