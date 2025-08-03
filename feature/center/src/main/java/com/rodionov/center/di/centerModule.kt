package com.rodionov.center.di

import com.rodionov.center.presentation.main.CenterViewModel
import com.rodionov.center.presentation.orientiring_competition_create.OrienteeringCreatorViewModel
import com.rodionov.domain.repository.OrienteeringCompetitionRepository
import com.rodionov.remote.repository.OrienteeringCompetitionRemoteRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val centerModule = module {
    viewModelOf(::CenterViewModel)
    viewModelOf(::OrienteeringCreatorViewModel)

}