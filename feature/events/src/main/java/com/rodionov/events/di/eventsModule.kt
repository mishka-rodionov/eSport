package com.rodionov.events.di

import com.rodionov.events.presentation.main.EventsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val eventsModule = module {
    viewModelOf(::EventsViewModel)
}