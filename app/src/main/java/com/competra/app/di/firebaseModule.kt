package com.competra.app.di

import com.competra.app.fcm.FcmTokenRegistry
import com.competra.app.fcm.FcmTokenRegistryImpl
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.dsl.module

val firebaseModule = module {
    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseMessaging.getInstance() }
    single<FcmTokenRegistry> { FcmTokenRegistryImpl() }
}
