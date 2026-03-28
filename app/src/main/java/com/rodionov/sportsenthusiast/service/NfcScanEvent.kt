package com.rodionov.sportsenthusiast.service

/**
 * Событие сканирования NFC-метки во время соревнования.
 */
sealed class NfcScanEvent {
    data class ParticipantScanned(
        val participantName: String,
        val startNumber: String,
        val groupName: String
    ) : NfcScanEvent()

    data class UnknownChip(val chipNumber: Int) : NfcScanEvent()

    data class ReadError(val message: String) : NfcScanEvent()
}
