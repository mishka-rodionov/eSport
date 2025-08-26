package com.rodionov.nfchelper

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity

interface SportiduinoHelper {

    var nfcMode: SportiduinoNfcMode

    fun setNfcAdapter(nfcAdapter: NfcAdapter)

    fun enableForegroundDispatch(activity: ComponentActivity, pendingIntent: PendingIntent)

    fun enableReaderMode(activity: ComponentActivity, handleTag: (Tag) -> Unit)

    fun disableReaderMode(activity: ComponentActivity)

    fun disableForegroundDispatch(activity: ComponentActivity)

    suspend fun subscribeToReadCard(handler: (String) -> Unit)

    suspend fun subscribeToWriteCard()

    suspend fun subscribeToStationSetting()

    suspend fun onNewTagDetected(tag: Tag)

}