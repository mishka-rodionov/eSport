package com.rodionov.nfchelper

import android.app.PendingIntent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import androidx.activity.ComponentActivity
import com.rodionov.nfchelper.nfccard.Card
import com.rodionov.nfchelper.nfccard.CardAdapter
import com.rodionov.nfchelper.nfccard.CardMifareClassic
import com.rodionov.nfchelper.nfccard.CardMifareUltralight
import com.rodionov.nfchelper.nfccard.ReadWriteCardException
import com.rodionov.resources.ResourceProvider
import kotlinx.coroutines.flow.MutableSharedFlow

class SportiduinoHelperImpl(
    private val resourceProvider: ResourceProvider
) : SportiduinoHelper {

    private var nfcAdapter: NfcAdapter? = null
    private val techList by lazy {
        arrayOf(
            arrayOf(MifareClassic::class.simpleName),
            arrayOf(MifareUltralight::class.simpleName)
        )
    }

    private val _readCardFlow = MutableSharedFlow<CharSequence>()

    override var nfcMode: SportiduinoNfcMode = SportiduinoNfcMode.READ_CARD

    override fun setNfcAdapter(nfcAdapter: NfcAdapter) {
        this.nfcAdapter = nfcAdapter
    }

    override fun enableForegroundDispatch(
        activity: ComponentActivity,
        pendingIntent: PendingIntent
    ) {
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, techList)
    }

    override fun enableReaderMode(activity: ComponentActivity, handleTag: (Tag) -> Unit) {
        nfcAdapter?.enableReaderMode(activity,
            { tag ->
                handleTag.invoke(tag)
            },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)
    }

    override fun disableForegroundDispatch(activity: ComponentActivity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    override suspend fun onNewTagDetected(tag: Tag) {
        when (nfcMode) {
            SportiduinoNfcMode.READ_CARD -> readCard(tag)
            SportiduinoNfcMode.WRITE_CARD -> {}
            SportiduinoNfcMode.STATION_SETTING -> {}
        }
    }

    override suspend fun subscribeToReadCard(handler: (String) -> Unit) {
        nfcMode = SportiduinoNfcMode.READ_CARD
        _readCardFlow.collect { handler.invoke(it.toString()) }
    }

    override suspend fun subscribeToWriteCard() {
        nfcMode = SportiduinoNfcMode.WRITE_CARD
    }

    override suspend fun subscribeToStationSetting() {
        nfcMode = SportiduinoNfcMode.STATION_SETTING
    }

    private suspend fun readCard(tag: Tag) {
        val tagInfo = StringBuilder()
        val tagId = tag.id
        for (b in tagId) {
            tagInfo.append(Integer.toHexString(b.toInt() and 0xFF)).append(" ")
        }
        val tagIdStr = tagInfo.toString()

        val techList = tag.techList
        var adapter: CardAdapter? = null
        techList.forEach { s ->
            if (s == MifareClassic::class.java.getName()) {
                adapter = CardMifareClassic(MifareClassic.get(tag))
            } else if (s == MifareUltralight::class.java.getName()) {
                adapter = CardMifareUltralight(MifareUltralight.get(tag))
            }
            if (adapter != null) {
                try {
                    adapter.connect()
                    val card = Card.detectCard(adapter, resourceProvider)
                    val buffer = card.read()
                    _readCardFlow.emit(card.parseData(buffer))
                } catch (e: ReadWriteCardException) {

                } finally {
                    adapter.close()
                }
                return@forEach
            }
        }
    }
}