package com.rodionov.core.sync.di

import com.rodionov.core.sync.ConflictResolver
import com.rodionov.core.sync.NetworkAvailabilityObserver
import com.rodionov.core.sync.SyncCenterWorker
import com.rodionov.core.sync.SyncOrchestrator
import com.rodionov.core.sync.SyncTriggerImpl
import com.rodionov.domain.sync.SyncTrigger
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val syncModule = module {
    singleOf(::ConflictResolver)
    singleOf(::SyncOrchestrator)
    singleOf(::NetworkAvailabilityObserver)
    single<SyncTrigger> { SyncTriggerImpl(androidContext(), get()) }
    workerOf(::SyncCenterWorker)
}
