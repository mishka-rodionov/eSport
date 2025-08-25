package com.rodionov.nfchelper.nfccard

import android.nfc.tech.TagTechnology
import java.io.IOException

abstract class CardAdapter(protected var tagTech: TagTechnology) {
    @JvmField
    var tagType: TagType = TagType.UNKNOWN

    @Throws(ReadWriteCardException::class)
    open fun connect() {
        try {
            if (!tagTech.isConnected) {
                tagTech.connect()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw ReadWriteCardException()
        }
    }

    fun close() {
        if (tagTech.isConnected) {
            try {
                tagTech.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(ReadWriteCardException::class)
    abstract fun readPages(
        firstPageIndex: Int,
        count: Int,
        stopIfPageNull: Boolean
    ): Array<ByteArray>

    @Throws(ReadWriteCardException::class)
    fun readPages(firstPageIndex: Int, count: Int): Array<ByteArray> {
        return readPages(firstPageIndex, count, false)
    }

    @Throws(ReadWriteCardException::class)
    fun readPage(firstPageIndex: Int): ByteArray {
        return readPages(firstPageIndex, 1)[0]
    }

    @Throws(ReadWriteCardException::class)
    abstract fun writePages(firstPageIndex: Int, data: Array<ByteArray>, count: Int)

    @Throws(ReadWriteCardException::class)
    fun writePage(firstPageIndex: Int, data: ByteArray) {
        writePages(firstPageIndex, arrayOf(data), 1)
    }

    val maxPage: Int
        get() {
            return when (tagType) {
                TagType.MIFARE_MINI -> 17
                TagType.MIFARE_1K -> 50
                TagType.MIFARE_4K -> 98
                TagType.MIFARE_UL, TagType.NTAG213 -> 39
                TagType.NTAG215 -> 129
                TagType.NTAG216 -> 225
                else -> 0
            }
        }

    @Throws(ReadWriteCardException::class)
    fun clearPage(page: Int) {
        val data = byteArrayOf(0, 0, 0, 0)
        writePage(page, data)
    }

    @Throws(ReadWriteCardException::class)
    fun clear(beginPage: Int, endPage: Int) {
        for (page in endPage downTo beginPage) {
            clearPage(page)
        }
    }
}
