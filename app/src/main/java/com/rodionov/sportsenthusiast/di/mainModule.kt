package com.rodionov.sportsenthusiast.di

import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.domain.repository.ResultConflictRepository
import com.rodionov.sportsenthusiast.presentation.main.MainViewModel
import com.rodionov.sportsenthusiast.service.CompetitionScanEventRepository
import com.rodionov.sportsenthusiast.service.CompetitionServiceControllerImpl
import com.rodionov.ui.CompetitionServiceController
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
    single { CompetitionScanEventRepository() }
    single<CompetitionServiceController> { CompetitionServiceControllerImpl() }
    single { ResultConflictRepository() }
    single { NetworkErrorRepository() }
}