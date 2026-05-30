package com.competra.nfchelper

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.activity.ComponentActivity
import com.competra.domain.models.orienteering.ReadChipData
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

    suspend fun subscribeToWriteCard(handler: (WriteChipResult) -> Unit)

    suspend fun subscribeToStationSetting(handler: (WriteChipResult) -> Unit)

    fun prepareWriteCard(cardNumber: Int, fastPunch: Boolean)

    fun prepareStationSetting(type: com.competra.nfchelper.nfccard.CardType, data: Array<ByteArray>)

    suspend fun onNewTagDetected(tag: Tag)

}