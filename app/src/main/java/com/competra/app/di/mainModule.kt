package com.competra.app.di

import com.competra.domain.repository.LoadingRepository
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.ResultConflictRepository
import com.competra.app.presentation.main.MainViewModel
import com.competra.app.service.CompetitionScanEventRepository
import com.competra.app.service.CompetitionStartAlertRepository
import com.competra.app.service.CompetitionServiceControllerImpl
import com.competra.ui.CompetitionServiceController
import com.competra.ui.CompetitionStartTimeRepository
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