package com.competra.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.Configuration
import com.competra.center.di.centerModule
import com.competra.core.sync.NetworkAvailabilityObserver
import com.competra.core.sync.SyncBootstrap
import com.competra.core.sync.di.syncModule
import com.competra.app.service.CompetitionForegroundService
import com.competra.data.navigation.di.navigationModule
import com.competra.eventdetails.di.eventDetailsModule
import com.competra.events.di.eventsModule
import com.competra.local.di.databaseModule
import com.competra.local.di.localModule
import com.competra.nfchelper.di.nfcModule
import com.competra.profile.di.profileModule
import com.competra.remote.di.authModule
import com.competra.remote.di.eventsDataModule
import com.competra.remote.di.orienteeringModule
import com.competra.remote.di.retrofitModule
import com.competra.remote.di.uploadModule
import com.competra.resources.di.resourceModule
import com.competra.app.di.mainModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

/**
 * Класс приложения Competra.
 * Выполняет инициализацию Koin (Dependency Injection) и создание каналов уведомлений.
 */
class CompetraApp : Application(), Configuration.Provider {

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
            androidContext(this@CompetraApp)
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
