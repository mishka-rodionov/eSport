package com.rodionov.sportsenthusiast

import android.app.Application
import com.rodionov.center.di.centerModule
import com.rodionov.data.navigation.di.navigationModule
import com.rodionov.events.di.eventsModule
import com.rodionov.local.di.databaseModule
import com.rodionov.remote.di.eventsDataModule
import com.rodionov.remote.di.orienteeringModule
import com.rodionov.remote.di.retrofitModule
import com.rodionov.resources.di.resourceModule
import com.rodionov.sportsenthusiast.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SportApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SportApp)
            //core modulles
            modules(retrofitModule, databaseModule, navigationModule, resourceModule)

            //data modules
            modules(orienteeringModule, eventsDataModule)

            //feature modules
            modules(mainModule, centerModule, eventsModule)
        }
    }
}