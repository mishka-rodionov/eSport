package com.competra.center.data.read_card

import com.competra.ui.BaseAction

sealed class OrientReadCardAction : BaseAction {
    data class EditSplitClicked(val index: Int) : OrientReadCardAction()
    data class SaveSplitEdit(val index: Int, val newTimestamp: Long) : OrientReadCardAction()
    data class DeleteSplit(val index: Int) : OrientReadCardAction()
    data object DismissEditSplit : OrientReadCardAction()
}
