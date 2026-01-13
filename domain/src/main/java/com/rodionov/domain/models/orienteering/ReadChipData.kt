package com.rodionov.domain.models.orienteering

sealed class ReadChipData {

    data class RawResult(
        val chipNumber: Int,
        val clearTime: Long,
        val splits: List<SplitTime>
    ): ReadChipData()

    data class MasterChipData(val data: String): ReadChipData()

}
