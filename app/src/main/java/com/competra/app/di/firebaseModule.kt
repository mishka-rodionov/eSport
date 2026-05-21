package com.competra.app.di


import com.competra.app.fcm.FcmTokenRegistryImpl
import com.competra.app.fcm.FcmTokenWorker
import com.competra.domain.repository.fcm.FcmTokenRegistry
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val firebaseModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseMessaging.getInstance() }
    single<FcmTokenRegistry> { FcmTokenRegistryImpl(androidContext(), get(), get()) }
    workerOf(::FcmTokenWorker)
}
