package com.rodionov.sportsenthusiast

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.Configuration
import com.rodionov.center.di.centerModule
import com.rodionov.core.sync.NetworkAvailabilityObserver
import com.rodionov.core.sync.SyncBootstrap
import com.rodionov.core.sync.di.syncModule
import com.rodionov.sportsenthusiast.service.CompetitionForegroundService
import com.rodionov.data.navigation.di.navigationModule
import com.rodionov.eventdetails.di.eventDetailsModule
import com.rodionov.events.di.eventsModule
import com.rodionov.local.di.databaseModule
import com.rodionov.local.di.localModule
import com.rodionov.nfchelper.di.nfcModule
import com.rodionov.profile.di.profileModule
import com.rodionov.remote.di.authModule
import com.rodionov.remote.di.eventsDataModule
import com.rodionov.remote.di.orienteeringModule
import com.rodionov.remote.di.retrofitModule
import com.rodionov.remote.di.uploadModule
import com.rodionov.resources.di.resourceModule
import com.rodionov.sportsenthusiast.di.mainModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

/**
 * Класс приложения Sports Enthusiast.
 * Выполняет инициализацию Koin (Dependency Injection) и создание каналов уведомлений.
 */
class SportApp : Application(), Configuration.Provider {

    private val networkObserver: NetworkAvailabilityObserver by inject()

    /**
     * On-demand Configuration: WorkManagerInitializer (отключённый в Manifest) больше не
     * вызывается, поэтому WorkManager при первом обращении подтягивает эту конфигурацию.
     * setWorkerFactory ставится через workManagerFactory() в Koin DSL.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SportApp)
            workManagerFactory()
            // core modules
            modules(
                retrofitModule, databaseModule, navigationModule, resourceModule, nfcModule,
                localModule, syncModule
            )

            // data modules
            modules(authModule, orienteeringModule, eventsDataModule, uploadModule)

            // feature modules
            modules(mainModule, centerModule, eventsModule, eventDetailsModule, profileModule)
        }

        createNotificationChannel()

        // Подписываемся на появление сети — каждое появление триггерит SyncCenterWorker.
        networkObserver.start { SyncBootstrap.enqueue(this) }
        // Дополнительный enqueue на старте — на случай, если сеть уже есть и есть unsynced.
        SyncBootstrap.enqueue(this)
    }

    /**
     * Создает канал уведомлений для работы сервиса соревнований.
     */
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
