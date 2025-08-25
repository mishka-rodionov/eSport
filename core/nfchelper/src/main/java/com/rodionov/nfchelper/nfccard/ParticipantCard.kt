package com.rodionov.nfchelper.nfccard

import com.rodionov.nfchelper.R
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INFO1
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INIT
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_INIT_TIME
import com.rodionov.nfchelper.nfccard.Constants.CARD_PAGE_START
import com.rodionov.nfchelper.nfccard.Constants.FAST_PUNCH_SIGN
import com.rodionov.nfchelper.nfccard.Constants.FW_PROTO_VERSION
import com.rodionov.resources.ResourceProvider

class ParticipantCard(
    adapter: CardAdapter,
    cardNumber: Int,
    fastPunch: Boolean,
    private val resourceProvider: ResourceProvider
) : Card(adapter) {
    var cardNumber: Int
    var cardInitTimestamp: Long = 0
    var fastPunch: Boolean

    constructor(
        adapter: CardAdapter,
        cardNumber: Int,
        fastPunch: Boolean,
        cardInitTimestamp: Long,
        resourceProvider: ResourceProvider
    ) : this(adapter, cardNumber, fastPunch, resourceProvider) {
        this.cardInitTimestamp = cardInitTimestamp
    }

    init {
        this.type = CardType.ORDINARY
        this.cardNumber = cardNumber
        this.fastPunch = fastPunch
    }

    @Throws(ReadWriteCardException::class)
    public override fun read(): Array<ByteArray> {
        if (cardNumber == 0) {
            type = CardType.UNKNOWN
            return Array<ByteArray>(0) { ByteArray(0) }
        }
        return adapter.readPages(CARD_PAGE_START, adapter.maxPage, true)
    }

    public override fun parseData(data: Array<ByteArray>): CharSequence {
        if (type == CardType.UNKNOWN) {
            return resourceProvider.getString(R.string.unknown_card_type)
        }

        var str: String? = resourceProvider.getString(R.string.participant_card_no_) + cardNumber
        if (fastPunch) {
            str += String.format(" (%s)", resourceProvider.getString(R.string.fast_punch))
        }
        str += "\n" + resourceProvider.getString(R.string.clear_time_) + Util.dformat.format(
            java.util.Date(cardInitTimestamp * 1000)
        )
        str += "\n" + resourceProvider.getString(R.string.record_count) + " %d"
        var recordCount = 0
        val timeHighPart = cardInitTimestamp and 0xFF000000L
        for (datum in data) {
            val cp = datum[0].toInt() and 0xFF
            if (cp == 0) {
                break
            }
            var punchTimestamp =
                (Util.toUint32(datum) and 0xFFFFFFL) + timeHighPart
            if (punchTimestamp < cardInitTimestamp) {
                punchTimestamp += 0x1000000
            }
            str += "\n"
            var cpStr = cp.toString()
            when (cp) {
                Config.START_STATION -> cpStr = resourceProvider.getString(R.string.start)
                Config.FINISH_STATION -> cpStr = resourceProvider.getString(R.string.finish)
            }
            str += String.format("%1$6s", cpStr)
            str += " - " + Util.dformat.format(
                java.util.Date(
                    punchTimestamp * 1000
                )
            )
            ++recordCount
        }
        return String.format(str, recordCount)
    }

    @Throws(ReadWriteCardException::class)
    override fun writeImpl() {
        adapter.clear(CARD_PAGE_INFO1, adapter.maxPage)
        if (fastPunch) {
            adapter.writePage(CARD_PAGE_INFO1, byteArrayOf(0, 0, 0, FAST_PUNCH_SIGN))
        }
        val currentTimestamp = System.currentTimeMillis() / 1000
        adapter.writePage(
            CARD_PAGE_INIT_TIME,
            Util.fromUint32(currentTimestamp)
        )
        if (cardNumber > 0) {  // else cleaning only
            val cardNumberArray = Util.fromUint16(cardNumber)
            val dataPageInit =
                byteArrayOf(cardNumberArray[0], cardNumberArray[1], 0, FW_PROTO_VERSION)
            adapter.writePage(CARD_PAGE_INIT, dataPageInit)
        }
    }
}
