package com.rodionov.nfchelper.nfccard

import com.rodionov.nfchelper.Password
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INFO1
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INIT
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INIT_TIME
import com.rodionov.nfchelper.nfccard.Constants.FAST_PUNCH_SIGN
import com.rodionov.nfchelper.nfccard.Constants.MASTER_CARD_SIGN


abstract class Card(var adapter: CardAdapter) {
    var type: CardType = CardType.UNKNOWN
    protected var dataPage4: ByteArray = "".toByteArray()

    @Throws(ReadWriteCardException::class)
    abstract fun read(): Array<ByteArray>
    abstract fun parseData(data: Array<ByteArray>): CharSequence

    @Throws(ReadWriteCardException::class)
    protected abstract fun writeImpl()

    @Throws(ReadWriteCardException::class)
    fun write() {
        adapter.connect()
        try {
            writeImpl()
        } finally {
            adapter.close()
        }
    }

    companion object {
        @Throws(ReadWriteCardException::class)
        fun detectCard(adapter: CardAdapter): Card {
            var data = adapter.readPage(CARD_PAGE_INIT)

            if (data[2] == MASTER_CARD_SIGN) {
                val type = CardType.byValue(Util.byteToUint(data[1]))
                val masterCard = MasterCard(adapter, type, Password.defaultPassword())
                masterCard.dataPage4 = data
                return masterCard
            } else {
                var cardNumber = data[0].toInt() and 0xFF
                cardNumber = cardNumber shl 8
                cardNumber = cardNumber or (data[1].toInt() and 0xFF)
                data = adapter.readPage(CARD_PAGE_INIT_TIME)
                val cardInitTimestamp: Long = Util.toUint32(data)
                data = adapter.readPage(CARD_PAGE_INFO1)
                val fastPunch = (data[3] == FAST_PUNCH_SIGN)
                return ParticipantCard(adapter, cardNumber, fastPunch, cardInitTimestamp)
            }
        }
    }
}

