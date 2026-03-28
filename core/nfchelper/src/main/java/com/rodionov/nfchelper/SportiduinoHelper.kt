package com.rodionov.nfchelper

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity
import com.rodionov.domain.models.orienteering.ReadChipData
import kotlinx.coroutines.flow.SharedFlow

interface SportiduinoHelper {

    val nfcErrorFlow: SharedFlow<String>

    var nfcMode: SportiduinoNfcMode

    fun setNfcAdapter(nfcAdapter: NfcAdapter)

    fun enableForegroundDispatch(activity: ComponentActivity, pendingIntent: PendingIntent)

    fun enableReaderMode(activity: ComponentActivity, handleTag: (Tag) -> Unit)

    fun disableReaderMode(activity: ComponentActivity)

    fun disableForegroundDispatch(activity: ComponentActivity)

    suspend fun subscribeToReadCard(handler: (ReadChipData) -> Unit)

    suspend fun subscribeToWriteCard()

    suspend fun subscribeToStationSetting()

    suspend fun onNewTagDetected(tag: Tag)

}