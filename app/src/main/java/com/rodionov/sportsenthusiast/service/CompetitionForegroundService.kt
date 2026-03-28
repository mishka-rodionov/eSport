package com.rodionov.sportsenthusiast.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.domain.models.orienteering.ReadChipData
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.sportsenthusiast.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Foreground Service для управления соревнованием.
 * Запускает секундомер в уведомлении и обрабатывает NFC-сканирования.
 */
class CompetitionForegroundService : Service() {

    private val sportiduinoHelper: SportiduinoHelper by inject()
    private val scanEventRepository: CompetitionScanEventRepository by inject()
    private val interactor: OrienteeringCompetitionInteractor by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var competitionId: Long = -1L
    private var startTimeMs: Long = 0L

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        competitionId = intent?.getLongExtra(EXTRA_COMPETITION_ID, -1L) ?: -1L
        startTimeMs = intent?.getLongExtra(EXTRA_START_TIME_MS, System.currentTimeMillis())
            ?: System.currentTimeMillis()

        startForeground(NOTIFICATION_ID, buildNotification(startTimeMs))
        subscribeToNfcEvents()

        return START_STICKY
    }

    private fun subscribeToNfcEvents() {
        serviceScope.launch {
            sportiduinoHelper.subscribeToReadCard { chipData ->
                serviceScope.launch { handleChipData(chipData) }
            }
        }

        serviceScope.launch {
            sportiduinoHelper.nfcErrorFlow.collect { errorMessage ->
                val event = NfcScanEvent.ReadError(errorMessage)
                scanEventRepository.emit(event)
                updateNotificationText("Ошибка: $errorMessage")
            }
        }
    }

    private suspend fun handleChipData(chipData: ReadChipData) {
        when (chipData) {
            is ReadChipData.RawResult -> {
                if (competitionId == -1L) return
                interactor.getParticipantByChipNumber(
                    competitionId = competitionId,
                    chipNumber = chipData.chipNumber
                ).onSuccess { participant ->
                    val name = "${participant.lastName} ${participant.firstName}"
                    val event = NfcScanEvent.ParticipantScanned(
                        participantName = name,
                        startNumber = participant.startNumber,
                        groupName = participant.groupName
                    )
                    scanEventRepository.emit(event)
                    updateNotificationText("$name • №${participant.startNumber}")
                }.onFailure {
                    val event = NfcScanEvent.UnknownChip(chipData.chipNumber)
                    scanEventRepository.emit(event)
                    updateNotificationText("Неизвестный чип: №${chipData.chipNumber}")
                }
            }
            is ReadChipData.MasterChipData -> {
                val event = NfcScanEvent.ReadError("Мастер-карта")
                scanEventRepository.emit(event)
                updateNotificationText("Отсканирована мастер-карта")
            }
        }
    }

    private fun buildNotification(startTimeMs: Long) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Соревнование идёт")
            .setContentText("Нет сканирований")
            .setWhen(startTimeMs)
            .setUsesChronometer(true)
            .setChronometerCountDown(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun updateNotificationText(text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Соревнование идёт")
            .setContentText(text)
            .setWhen(startTimeMs)
            .setUsesChronometer(true)
            .setChronometerCountDown(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext[Job]?.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "competition_running"
        const val EXTRA_COMPETITION_ID = "competition_id"
        const val EXTRA_START_TIME_MS = "start_time_ms"

        fun startIntent(context: Context, competitionId: Long, startTimeMs: Long): Intent =
            Intent(context, CompetitionForegroundService::class.java).apply {
                putExtra(EXTRA_COMPETITION_ID, competitionId)
                putExtra(EXTRA_START_TIME_MS, startTimeMs)
            }
    }
}
