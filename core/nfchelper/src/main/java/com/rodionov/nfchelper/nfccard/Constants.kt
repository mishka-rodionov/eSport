package com.rodionov.nfchelper.nfccard

object Constants {
    const val CARD_PAGE_INIT: Int = 4
    const val CARD_PAGE_INIT_TIME: Int = 5
    const val CARD_PAGE_LAST_RECORD_INFO: Int = 6
    const val CARD_PAGE_INFO1: Int = 6
    const val CARD_PAGE_INFO2: Int = 7
    const val CARD_PAGE_START: Int = 8
    const val CARD_PAGE_PASS: Int = 5
    const val CARD_PAGE_DATE: Int = 6
    const val CARD_PAGE_TIME: Int = 7
    const val CARD_PAGE_STATION_NUM: Int = 6
    const val CARD_PAGE_BACKUP_START: Int = 6
    val MASTER_CARD_SIGN: Byte = 0xFF.toByte()
    val FAST_PUNCH_SIGN: Byte = 0xAA.toByte()
    val FW_PROTO_VERSION: Byte = 8.toByte()
    const val OPERATED_YEAR_MIN: Int = 2000
}
