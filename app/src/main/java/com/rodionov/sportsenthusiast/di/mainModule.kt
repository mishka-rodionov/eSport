package com.rodionov.sportsenthusiast.di

import com.rodionov.domain.repository.LoadingRepository
import com.rodionov.domain.repository.NetworkErrorRepository
import com.rodionov.domain.repository.ResultConflictRepository
import com.rodionov.sportsenthusiast.presentation.main.MainViewModel
import com.rodionov.sportsenthusiast.service.CompetitionScanEventRepository
import com.rodionov.sportsenthusiast.service.CompetitionStartAlertRepository
import com.rodionov.sportsenthusiast.service.CompetitionServiceControllerImpl
import com.rodionov.ui.CompetitionServiceController
import com.rodionov.ui.CompetitionStartTimeRepository
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
    single { CompetitionScanEventRepository() }
    single { CompetitionStartAlertRepository() }
    single<CompetitionServiceController> { CompetitionServiceControllerImpl() }
    single { CompetitionStartTimeRepository() }
    single { ResultConflictRepository() }
    single { NetworkErrorRepository() }
    single { LoadingRepository() }
}