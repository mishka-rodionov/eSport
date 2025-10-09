package com.rodionov.center.presentation.read_card

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.read_card.OrientReadCardState
import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper
): BaseViewModel<OrientReadCardState>(OrientReadCardState()) {

    override fun onAction(action: BaseAction) {

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sportiduinoHelper.subscribeToReadCard { text ->
                updateState { copy(text = text) }
            }
        }
    }

}