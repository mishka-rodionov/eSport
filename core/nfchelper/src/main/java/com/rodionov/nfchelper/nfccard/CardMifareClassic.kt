package com.rodionov.nfchelper.nfccard

import android.nfc.tech.MifareClassic
import android.util.Log
import java.io.IOException

class CardMifareClassic(private val tag: MifareClassic) : CardAdapter(tag) {
    val numOfBlockInSector: Int = 4
    var lastSectorAuth: Int = -1

    init {
        val size = tag.size
        //Log.d("mfc", String.format("timeout: %d", tag.getTimeout()));
        tagType = when (size) {
            MifareClassic.SIZE_1K, MifareClassic.SIZE_2K -> TagType.MIFARE_1K
            MifareClassic.SIZE_4K -> TagType.MIFARE_4K
            MifareClassic.SIZE_MINI -> TagType.MIFARE_MINI
            else -> TagType.UNKNOWN
        }
    }

    @Throws(ReadWriteCardException::class)
    public override fun readPages(
        firstPageIndex: Int,
        count: Int,
        stopIfPageNull: Boolean
    ): Array<ByteArray> {
        val blockData = Array(count) { ByteArray(MifareClassic.BLOCK_SIZE) }
        val firstBlockIndex = firstPageIndex - 3 + (firstPageIndex - 3) / 3
        var i = 0
        for (blockIndex in firstBlockIndex..<tag.getBlockCount()) {
            if ((blockIndex + 1) % numOfBlockInSector == 0) {
                continue
            }
            val nAttempts = 3
            for (j in 0..<nAttempts) {
                try {
                    authenticateSectorIfNeed(tag.blockToSector(blockIndex))
                    blockData[i] = tag.readBlock(blockIndex)
                    //Log.d("CardMifareClassic", i + ": " +
                    //    Integer.toHexString(blockData[i][0] & 0xff) + " " +
                    //    Integer.toHexString(blockData[i][1] & 0xff) + " " +
                    //    Integer.toHexString(blockData[i][2] & 0xff) + " " +
                    //    Integer.toHexString(blockData[i][3] & 0xff));
                } catch (e: IOException) {
                    Log.d("mfc", e.toString())
                    if (j == nAttempts - 1) {
                        //e.printStackTrace();
                        throw ReadWriteCardException()
                    }
                } catch (e: ReadWriteCardException) {
                    Log.d("mfc", e.toString())
                    if (j == nAttempts - 1) {
                        throw ReadWriteCardException()
                    }
                }
            }
            if (stopIfPageNull
                && blockData[i][0].toInt() == 0 && blockData[i][1].toInt() == 0 && blockData[i][2].toInt() == 0 && blockData[i][3].toInt() == 0
            ) {
                break
            }
            ++i
            if (i >= count) {
                break
            }
        }
        return blockData
    }

    @Throws(ReadWriteCardException::class)
    public override fun writePages(firstPageIndex: Int, data: Array<ByteArray>, count: Int) {
        val firstBlockIndex = firstPageIndex - 3 + (firstPageIndex - 3) / 3
        var i = 0
        var blockIndex = firstBlockIndex
        while (i < count) {
            if ((blockIndex + 1) % numOfBlockInSector == 0) {
                ++blockIndex
                continue
            }
            var pageData = data[i++]
            if (pageData.size < MifareClassic.BLOCK_SIZE) {
                pageData = pageData.copyOf(MifareClassic.BLOCK_SIZE)
            }

            val nAttempts = 5
            for (j in 0..<nAttempts) {
                try {
                    if (!tag.isConnected) {
                        connect()
                        Log.d("mfc", "connect")
                    }
                    authenticateSectorIfNeed(tag.blockToSector(blockIndex))
                    //Log.d("mfc", String.format("writeBlock: %d, %b", blockIndex, tag.isConnected()));
                    tag.writeBlock(blockIndex, pageData)
                    break
                } catch (e: IOException) {
                    Log.d(
                        "mfc",
                        String.format("writeBlock %d failed, attempt %d", blockIndex, j + 1)
                    )
                    Log.d("mfc", e.toString())
                    if (j == nAttempts - 1) {
                        //e.printStackTrace();
                        throw ReadWriteCardException()
                    }
                    lastSectorAuth = -1
                    close()
                } catch (e: ReadWriteCardException) {
                    Log.d(
                        "mfc",
                        String.format("writeBlock %d failed, attempt %d", blockIndex, j + 1)
                    )
                    Log.d("mfc", e.toString())
                    if (j == nAttempts - 1) {
                        throw ReadWriteCardException()
                    }
                    lastSectorAuth = -1
                    close()
                }
            }
            ++blockIndex
        }
    }

    @Throws(ReadWriteCardException::class)
    private fun authenticateSectorIfNeed(sector: Int) {
        if (sector != lastSectorAuth) {
            lastSectorAuth = sector
            try {
                //Log.d("mfc", String.format("authenticate sector: %d, %b", sector, tag.isConnected()));
                if (!tag.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT)) {
                    Log.d(
                        "CardMifareClassic", "authenticateSectorWithKeyA failed, sector " +
                                sector
                    )
                    lastSectorAuth = -1
                    throw ReadWriteCardException()
                }
            } catch (e: IOException) {
                Log.d("mfc", e.toString())
                //e.printStackTrace();
                throw ReadWriteCardException()
            }
        }
    }
}
