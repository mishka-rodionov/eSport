package com.competra.app.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.competra.app.R
import com.competra.app.presentation.main.MainActivity
import com.competra.domain.models.push.PushKind
import com.competra.domain.repository.fcm.FcmTokenRegistry
import com.competra.domain.repository.push.PushPreferencesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.atomic.AtomicInteger

/**
 * Принимает push-уведомления от FCM.
 *
 * При foreground-состоянии приложения вызывается [onMessageReceived] независимо от
 * наличия notification-payload. При background — если payload содержит notification,
 * Android рисует уведомление сам, иначе вызывается [onMessageReceived].
 */
class CompetraMessagingService : FirebaseMessagingService() {

    private val tokenRegistry: FcmTokenRegistry by inject()
    private val pushPreferencesRepository: PushPreferencesRepository by inject()

    override fun onNewToken(token: String) {
        tokenRegistry.submit(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"].orEmpty()
        val body = message.notification?.body ?: message.data["body"].orEmpty()
        if (title.isEmpty() && body.isEmpty()) return

        val kind = message.data["kind"]
        val competitionId = message.data["competition_id"]

        CoroutineScope(Dispatchers.IO).launch {
            val category = PushKind.toPushCategory(kind)
            if (category != null && !pushPreferencesRepository.isEnabled(category)) return@launch
            showNotification(title, body, kind, competitionId)
        }
    }

    private fun showNotification(title: String, body: String, kind: String?, competitionId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_PUSH_KIND, kind)
            putExtra(MainActivity.EXTRA_PUSH_COMPETITION_ID, competitionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationIdCounter.incrementAndGet(), notification)
    }

    companion object {
        const val PUSH_CHANNEL_ID = "competra_push"
        private val notificationIdCounter = AtomicInteger(1000)
    }
}
