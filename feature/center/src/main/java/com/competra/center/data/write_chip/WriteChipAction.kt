package com.competra.center.data.write_chip

import com.competra.nfchelper.WriteChipResult
import com.competra.ui.BaseAction

sealed class WriteChipAction : BaseAction {
    data class SelectTab(val tab: WriteChipTab) : WriteChipAction()
    data class ChipNumberChanged(val value: String) : WriteChipAction()
    data class FastPunchToggled(val enabled: Boolean) : WriteChipAction()
    data class StationNumberChanged(val value: String) : WriteChipAction()
    data class WakeUpTimeChanged(val ms: Long) : WriteChipAction()
    data class NewPasswordChanged(val p0: Int, val p1: Int, val p2: Int) : WriteChipAction()

    data object StartClearChip : WriteChipAction()
    data object StartWriteNumber : WriteChipAction()
    data object StartSetStationTime : WriteChipAction()
    data object StartSetStationNumber : WriteChipAction()
    data object StartSetStationConfig : WriteChipAction()
    data object StartSleep : WriteChipAction()
    data object StartGetState : WriteChipAction()
    data object StartSetPassword : WriteChipAction()

    data class NfcWriteResult(val result: WriteChipResult) : WriteChipAction()
    data class NfcStationResult(val result: WriteChipResult) : WriteChipAction()
}
