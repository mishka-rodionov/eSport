package com.rodionov.sportsenthusiast.presentation.main

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.data.navigation.BaseNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.nfchelper.SportiduinoHelper
import kotlinx.coroutines.launch

class MainViewModel(
    private val navigation: Navigation,
    private val sportiduinoHelper: SportiduinoHelper
): ViewModel() {

    suspend fun collectNavigationEffect(navigationHandler: (BaseNavigation) -> Unit, destination: BaseNavigation) {
//        viewModelScope.launch(Dispatchers.Main) {
            navigation.collectNavigationEffect(navigationHandler, destination)
//        }
    }

    fun navigate() {

    }

    fun setNfcAdapter(nfcAdapter: NfcAdapter) = sportiduinoHelper.setNfcAdapter(nfcAdapter)

    fun enableForegroundDispatch(activity: ComponentActivity, pendingIntent: PendingIntent) {
        sportiduinoHelper.enableForegroundDispatch(activity, pendingIntent)
    }

    fun enableReaderMode(activity: ComponentActivity, handleTag: (Tag) -> Unit) {
        sportiduinoHelper.enableReaderMode(activity, handleTag)
    }

    fun disableForegroundDispatch(activity: ComponentActivity) = sportiduinoHelper.disableForegroundDispatch(activity)

    fun onNewTagDetected(tag: Tag) {
        viewModelScope.launch {
            sportiduinoHelper.onNewTagDetected(tag)
        }
    }

}