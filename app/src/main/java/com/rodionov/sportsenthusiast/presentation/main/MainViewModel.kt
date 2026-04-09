package com.rodionov.sportsenthusiast.presentation.main

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.orienteering.ResultConflictEvent
import com.rodionov.domain.repository.ResultConflictRepository
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.sportsenthusiast.service.CompetitionScanEventRepository
import com.rodionov.sportsenthusiast.service.NfcScanEvent
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.CompetitionServiceCommand
import com.rodionov.ui.CompetitionServiceController
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Главная вьюмодель приложения.
 * Управляет общими процессами: навигацией, NFC и состоянием авторизации.
 *
 * @property navigation Интерфейс управления навигацией.
 * @property sportiduinoHelper Помощник для работы с NFC оборудованием.
 * @property serviceController Контроллер управления foreground-сервисом соревнования.
 * @property scanEventRepository Репозиторий событий NFC-сканирования.
 */
class MainViewModel(
    private val navigation: Navigation,
    private val sportiduinoHelper: SportiduinoHelper,
    private val serviceController: CompetitionServiceController,
    private val scanEventRepository: CompetitionScanEventRepository,
    private val resultConflictRepository: ResultConflictRepository
) : BaseViewModel<BaseState>(object : BaseState {}) {

    /**
     * Поток базовых эффектов навигации (например, BackRoute).
     */
    val baseNavigationEffect: SharedFlow<BaseNavigation> = navigation.baseNavigationEffect

    /**
     * Поток команд переключения таба нижней навигации.
     */
    val switchTabEffect: SharedFlow<String> = navigation.switchTabEffect

    /**
     * Команды запуска/остановки foreground-сервиса, читаются в MainActivity.
     */
    val serviceCommands: SharedFlow<CompetitionServiceCommand> = serviceController.commands

    private val _currentScanEvent = MutableStateFlow<NfcScanEvent?>(null)

    /**
     * Текущее событие NFC-сканирования для отображения баннера. Обнуляется через 4 секунды.
     */
    val currentScanEvent: StateFlow<NfcScanEvent?> = _currentScanEvent.asStateFlow()

    private val _conflictEvent = MutableStateFlow<ResultConflictEvent?>(null)

    /**
     * Текущее событие конфликта результата для отображения bottom sheet диалога.
     */
    val conflictEvent: StateFlow<ResultConflictEvent?> = _conflictEvent.asStateFlow()

    init {
        viewModelScope.launch {
            scanEventRepository.events.collect { event ->
                _currentScanEvent.value = event
                delay(4000)
                _currentScanEvent.compareAndSet(event, null)
            }
        }
        viewModelScope.launch {
            resultConflictRepository.events.collect { event ->
                _conflictEvent.value = event
            }
        }
    }

    /** Применяет конфликтующий результат: вызывает onApply и закрывает диалог. */
    fun applyConflict() {
        val event = _conflictEvent.value ?: return
        viewModelScope.launch {
            event.onApply()
            _conflictEvent.value = null
        }
    }

    /** Отменяет конфликт без сохранения данных. */
    fun cancelConflict() {
        _conflictEvent.value = null
    }

    override fun onAction(action: BaseAction) {
        // Базовая обработка действий
    }

    /**
     * Подписка на специфичные для модуля эффекты навигации.
     */
    suspend fun collectNavigationEffect(navigationHandler: (BaseNavigation) -> Unit, destination: BaseNavigation) {
//        viewModelScope.launch(Dispatchers.Main) {
        navigation.collectNavigationEffect(navigationHandler, destination)
//        }
    }

    fun setNfcAdapter(nfcAdapter: NfcAdapter) = sportiduinoHelper.setNfcAdapter(nfcAdapter)

    fun enableReaderMode(activity: ComponentActivity, handleTag: (Tag) -> Unit) {
        sportiduinoHelper.enableReaderMode(activity, handleTag)
    }

    fun disableReaderMode(activity: ComponentActivity) = sportiduinoHelper.disableReaderMode(activity)

    fun onNewTagDetected(tag: Tag) {
        viewModelScope.launch {
            sportiduinoHelper.onNewTagDetected(tag)
        }
    }
}
