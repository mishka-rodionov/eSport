package com.rodionov.sportsenthusiast

import android.app.Application
import com.rodionov.center.di.centerModule
import com.rodionov.data.navigation.di.navigationModule
import com.rodionov.sportsenthusiast.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SportApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SportApp)
            modules(
                listOf(
                    navigationModule,
                    mainModule,
                    centerModule
                )
            )
        }
    }
}