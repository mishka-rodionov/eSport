package com.rodionov.sportsenthusiast

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.rodionov.center.di.centerModule
import com.rodionov.sportsenthusiast.service.CompetitionForegroundService
import com.rodionov.data.navigation.di.navigationModule
import com.rodionov.events.di.eventsModule
import com.rodionov.local.di.databaseModule
import com.rodionov.local.di.localModule
import com.rodionov.nfchelper.di.nfcModule
import com.rodionov.profile.di.profileModule
import com.rodionov.remote.di.authModule
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
            //core modules
            modules(
                retrofitModule, databaseModule, navigationModule, resourceModule, nfcModule,
                localModule
            )

            //data modules
            modules(authModule, orienteeringModule, eventsDataModule)

            //feature modules
            modules(mainModule, centerModule, eventsModule, profileModule)
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CompetitionForegroundService.CHANNEL_ID,
            "Соревнование",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Информация о текущем соревновании" }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}