package com.rodionov.nfchelper

import android.nfc.Tag

interface SportiduinoHelper {

    var nfcMode: SportiduinoNfcMode

    fun subscribeToReadCard()

    fun subscribeToWriteCard()

    fun subscribeToStationSetting()

    fun onNewTagDetected(tag: Tag)

}