package com.rodionov.center.di

import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.presentation.event_control.orienteering.OrienteeringEventControlViewModel
import com.rodionov.center.presentation.main.CenterViewModel
import com.rodionov.center.presentation.orientiring_competition_create.OrienteeringCreatorViewModel
import com.rodionov.center.presentation.read_card.OrientReadCardViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val centerModule = module {
    viewModelOf(::CenterViewModel)
    viewModelOf(::OrienteeringCreatorViewModel)
    viewModelOf(::OrienteeringEventControlViewModel)
    viewModelOf(::OrientReadCardViewModel)
    factoryOf(::OrienteeringCompetitionInteractor)
}