package com.competra.core.sync.di

import com.competra.core.sync.ConflictResolver
import com.competra.core.sync.NetworkAvailabilityObserver
import com.competra.core.sync.SyncCenterWorker
import com.competra.core.sync.SyncOrchestrator
import com.competra.core.sync.SyncTriggerImpl
import com.competra.domain.sync.SyncTrigger
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
