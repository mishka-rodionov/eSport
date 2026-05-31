package com.competra.center.data.read_card

import com.competra.ui.BaseAction

sealed class OrientReadCardAction : BaseAction {
    data class EditSplitClicked(val index: Int) : OrientReadCardAction()
    data class SaveSplitEdit(val index: Int, val newTimestamp: Long) : OrientReadCardAction()
    data class DeleteSplit(val index: Int) : OrientReadCardAction()
    data object DismissEditSplit : OrientReadCardAction()
    /** Засчитать пропущенный КП с временем предыдущей отметки (или старта). */
    data class CreditMissedCp(val cpNumber: Int, val prevTimestamp: Long) : OrientReadCardAction()
    /** Явное сохранение DSQ-результата в БД после проверки организатором. */
    data object SaveResult : OrientReadCardAction()
}
