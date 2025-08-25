package com.rodionov.nfchelper

import android.nfc.NfcAdapter
import android.nfc.Tag

class SportiduinoHelperImpl: SportiduinoHelper {

    private var nfcAdapter: NfcAdapter? = null

    override var nfcMode: SportiduinoNfcMode
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun onNewTagDetected(tag: Tag) {
        when(nfcMode) {
            SportiduinoNfcMode.READ_CARD -> {}
            SportiduinoNfcMode.WRITE_CARD -> {}
            SportiduinoNfcMode.STATION_SETTING -> {}
        }
    }

    override fun subscribeToReadCard() {
        nfcMode = SportiduinoNfcMode.READ_CARD
    }

    override fun subscribeToWriteCard() {
        nfcMode = SportiduinoNfcMode.WRITE_CARD
    }

    override fun subscribeToStationSetting() {
        nfcMode = SportiduinoNfcMode.STATION_SETTING
    }
}