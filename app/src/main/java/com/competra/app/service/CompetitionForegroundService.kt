package com.competra.app.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.ReadChipData
import com.competra.nfchelper.SportiduinoHelper
import com.competra.app.R
import com.competra.ui.CompetitionStartTimeRepository
import kotlin.math.ceil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Foreground Service для управления соревнованием.
 * Запускает секундомер в уведомлении, отслеживает стартовые времена участников,
 * воспроизводит звуковые сигналы и обрабатывает NFC-сканирования.
 */
class CompetitionForegroundService : Service() {

    private val sportiduinoHelper: SportiduinoHelper by inject()
    private val scanEventRepository: CompetitionScanEventRepository by inject()
    private val startAlertRepository: CompetitionStartAlertRepository by inject()
    private val startTimeRepository: CompetitionStartTimeRepository by inject()
    private val interactor: OrienteeringCompetitionInteractor by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var competitionId: String? = null
    private var startTimeMs: Long = 0L

    /** Текст уведомления о последнем финишировавшем (из NFC-скана). */
    @Volatile private var lastScanNotificationText: String? = null

    /** Следующий стартующий участник — обновляется мониторингом. */
    @Volatile private var nextStarterText: String? = null

    private val toneGenerator: ToneGenerator? by lazy {
        try { ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME) }
        catch (_: Exception) { null }
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val newCompetitionId = intent?.getStringExtra(EXTRA_COMPETITION_ID)
        val newStartTimeMs = intent?.getLongExtra(EXTRA_START_TIME_MS, 0L) ?: 0L

        // Обновляем поля только при наличии валидных значений в Intent.
        // Если Intent == null (edge-case при START_REDELIVER_INTENT), не загрязняем
        // startTimeRepository некорректным System.currentTimeMillis().
        if (!newCompetitionId.isNullOrEmpty()) competitionId = newCompetitionId
        if (newStartTimeMs > 0L) {
            startTimeMs = newStartTimeMs
            startTimeRepository.set(startTimeMs)
        }

        startForeground(NOTIFICATION_ID, buildNotification(""))
        subscribeToNfcEvents()

        if (!competitionId.isNullOrEmpty()) {
            launchParticipantMonitoring()
        }

        return START_REDELIVER_INTENT
    }

    // ───────────────────────────────────────────────────────── NFC ──

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
            }
        }
    }

    private suspend fun handleChipData(chipData: ReadChipData) {
        when (chipData) {
            is ReadChipData.RawResult -> {
                val id = competitionId ?: return
                interactor.getParticipantByChipNumber(
                    competitionId = id,
                    chipNumber = chipData.chipNumber
                ).onSuccess { participant ->
                    val name = "${participant.lastName} ${participant.firstName}"
                    val event = NfcScanEvent.ParticipantScanned(
                        participantName = name,
                        startNumber = participant.startNumber,
                        groupName = participant.groupName
                    )
                    scanEventRepository.emit(event)
                    // Сохраняем текст последнего финишировавшего для уведомления
                    lastScanNotificationText = "Финиш: $name №${participant.startNumber}"
                }.onFailure {
                    scanEventRepository.emit(NfcScanEvent.UnknownChip(chipData.chipNumber))
                }
            }
            is ReadChipData.MasterChipData -> {
                scanEventRepository.emit(NfcScanEvent.ReadError("Мастер-карта"))
            }
        }
    }

    // ─────────────────────────────────────── Monitoring participants ──

    private fun launchParticipantMonitoring() {
        val id = competitionId ?: return
        serviceScope.launch {
            val participants = interactor.getParticipants(id)
                .getOrNull()
                .orEmpty()
                .filter { it.startTime > 0 }
                .sortedBy { it.startTime }

            if (participants.isEmpty()) return@launch

            monitorParticipants(participants)
        }
    }

    private suspend fun CoroutineScope.monitorParticipants(
        participants: List<OrienteeringParticipant>
    ) {
        val playedSounds = mutableSetOf<String>()
        var lastNotificationSecond = -1L

        while (isActive) {
            val now = System.currentTimeMillis()

            // Участники, которые ещё не стартовали
            val upcoming = participants.filter { it.startTime > now }

            // Участники, которые стартовали в последние 2 секунды
            val justStarted = participants.filter {
                it.startTime <= now && it.startTime > now - 2000
            }

            // Звук старта (длинный сигнал) и событие для UI
            justStarted.forEach { p ->
                val key = "${p.id}_started"
                if (playedSounds.add(key)) {
                    playLongBeep()
                    startAlertRepository.emit(
                        ParticipantStartAlert.Started(
                            participantName = "${p.lastName} ${p.firstName}",
                            startNumber = p.startNumber
                        )
                    )
                }
            }

            val nextStarter = upcoming.firstOrNull()
            val nextNextStarter = upcoming.getOrNull(1)

            if (nextStarter != null) {
                val msUntilStart = nextStarter.startTime - now
                // ceil обеспечивает равномерность: seconds=N срабатывает когда msUntilStart ≤ N*1000,
                // что гарантирует ровно ~1000ms между сигналами независимо от фазы polling
                val secondsUntilStart = ceil(msUntilStart.toDouble() / 1000).toInt()

                // Короткие звуковые сигналы за 10, 5, 4, 3, 2, 1 секунды
                val soundTargets = setOf(10, 5, 4, 3, 2, 1)
                if (secondsUntilStart in soundTargets) {
                    val key = "${nextStarter.id}_$secondsUntilStart"
                    if (playedSounds.add(key)) playShortBeep()
                }

                // Событие для UI-баннера в диапазоне 1..10 секунд
                // (0 не используется: при msUntilStart→0 участник переходит в justStarted → GO)
                if (secondsUntilStart in 1..10) {
                    startAlertRepository.emit(
                        ParticipantStartAlert.Upcoming(
                            participantName = "${nextStarter.lastName} ${nextStarter.firstName}",
                            startNumber = nextStarter.startNumber,
                            countdownSeconds = secondsUntilStart,
                            nextParticipantName = nextNextStarter?.let {
                                "${it.lastName} ${it.firstName}"
                            },
                            nextStartNumber = nextNextStarter?.startNumber
                        )
                    )
                }

                // Строка для уведомления
                nextStarterText = if (secondsUntilStart in 0..10) {
                    "СТАРТ: №${nextStarter.startNumber} ${nextStarter.lastName} • ${secondsUntilStart}с"
                } else {
                    val minutes = secondsUntilStart / 60
                    val seconds = secondsUntilStart % 60
                    "→ №${nextStarter.startNumber} ${nextStarter.lastName} И. • %02d:%02d".format(minutes, seconds)
                }
            } else {
                nextStarterText = null
            }

            // Обновляем уведомление раз в секунду
            val currentSecond = now / 1000
            if (currentSecond != lastNotificationSecond) {
                lastNotificationSecond = currentSecond
                val notifText = nextStarterText ?: lastScanNotificationText ?: ""
                updateNotification(notifText)
            }

            delay(200)
        }
    }

    // ─────────────────────────────────────────────── Sound helpers ──

    private fun playShortBeep() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
    }

    private fun playLongBeep() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 900)
    }

    // ─────────────────────────────────────────── Notification helpers ──

    private fun buildNotification(contentText: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Соревнование идёт")
            .setContentText(contentText)
            .setWhen(startTimeMs)
            .setUsesChronometer(true)
            .setChronometerCountDown(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun updateNotification(contentText: String) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    // ──────────────────────────────────────────────────── Lifecycle ──

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        serviceScope.cancel()
        startTimeRepository.clear()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "competition_running"
        const val EXTRA_COMPETITION_ID = "competition_id"
        const val EXTRA_START_TIME_MS = "start_time_ms"

        fun startIntent(context: Context, competitionId: String, startTimeMs: Long): Intent =
            Intent(context, CompetitionForegroundService::class.java).apply {
                putExtra(EXTRA_COMPETITION_ID, competitionId)
                putExtra(EXTRA_START_TIME_MS, startTimeMs)
            }
    }
}
