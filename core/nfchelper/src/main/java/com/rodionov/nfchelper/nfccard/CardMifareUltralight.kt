package com.rodionov.nfchelper.nfccard

import android.nfc.tech.MifareUltralight
import android.nfc.tech.TagTechnology
import java.io.IOException

class CardMifareUltralight(private val tag: MifareUltralight) : CardAdapter(tag as TagTechnology) {
    @Throws(ReadWriteCardException::class)
    public override fun connect() {
        super.connect()

        val pageData = readPage(3)

        tagType = when (pageData[2].toInt() and 0xFF) {
            0x12 -> TagType.NTAG213
            0x3e -> TagType.NTAG215
            0x6d -> TagType.NTAG216
            else -> TagType.MIFARE_UL
        }
    }

    @Throws(ReadWriteCardException::class)
    public override fun readPages(
        firstPageIndex: Int,
        count: Int,
        stopIfPageNull: Boolean
    ): Array<ByteArray> {
        val blockData = Array<ByteArray>(count) { ByteArray(16) }
        var pageIndex = firstPageIndex
        for (i in 0..<count) {
            try {
                blockData[i] = tag.readPages(pageIndex++)
            } catch (e: IOException) {
                e.printStackTrace()
                throw ReadWriteCardException()
            }
            if (stopIfPageNull
                && blockData[i][0].toInt() == 0 && blockData[i][1].toInt() == 0 && blockData[i][2].toInt() == 0 && blockData[i][3].toInt() == 0
            ) {
                break
            }
        }
        return blockData
    }

    @Throws(ReadWriteCardException::class)
    public override fun writePages(firstPageIndex: Int, data: Array<ByteArray>, count: Int) {
        var pageIndex = firstPageIndex
        for (i in 0..<count) {
            var pageData = data[i]
            if (pageData.size < MifareUltralight.PAGE_SIZE) {
                pageData = pageData.copyOf(MifareUltralight.PAGE_SIZE)
            }
            try {
                tag.writePage(pageIndex++, pageData)
            } catch (e: IOException) {
                e.printStackTrace()
                throw ReadWriteCardException()
            }
        }
    }
}
