package com.rodionov.sportsenthusiast.presentation.main

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Главная вьюмодель приложения.
 * Управляет общими процессами: навигацией, NFC и состоянием авторизации.
 * 
 * @property navigation Интерфейс управления навигацией.
 * @property sportiduinoHelper Помощник для работы с NFC оборудованием.
 */
class MainViewModel(
    private val navigation: Navigation,
    private val sportiduinoHelper: SportiduinoHelper
): BaseViewModel<BaseState>(object : BaseState{}) {

    /**
     * Поток базовых эффектов навигации (например, BackRoute).
     */
    val baseNavigationEffect: SharedFlow<BaseNavigation> = navigation.baseNavigationEffect

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
