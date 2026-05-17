package com.competra.events.di

import com.competra.events.presentation.main.EventsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val eventsModule = module {
    viewModelOf(::EventsViewModel)
}
