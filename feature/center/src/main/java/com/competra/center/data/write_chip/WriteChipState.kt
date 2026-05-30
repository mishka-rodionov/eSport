package com.competra.center.data.write_chip

import com.competra.ui.BaseState

data class WriteChipState(
    val selectedTab: WriteChipTab = WriteChipTab.CLEAR,
    val chipNumberInput: String = "",
    val fastPunch: Boolean = false,
    val stationNumberInput: String = "",
    val wakeUpDateTimeMs: Long = System.currentTimeMillis() + 3_600_000L,
    val newPassword: Triple<Int, Int, Int> = Triple(0, 0, 0),
    val isWaitingForChip: Boolean = false,
    val lastResult: WriteChipOperationResult? = null
) : BaseState

enum class WriteChipTab { CLEAR, WRITE_NUMBER, STATION }

sealed class WriteChipOperationResult {
    data class Success(val message: String) : WriteChipOperationResult()
    data class Error(val message: String) : WriteChipOperationResult()
}
