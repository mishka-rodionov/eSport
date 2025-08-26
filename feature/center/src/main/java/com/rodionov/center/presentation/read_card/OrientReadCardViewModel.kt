package com.rodionov.center.presentation.read_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.read_card.OrientReadCardState
import com.rodionov.nfchelper.SportiduinoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrientReadCardViewModel(
    private val sportiduinoHelper: SportiduinoHelper
): ViewModel() {

    private val _state = MutableStateFlow(OrientReadCardState())
    val state: StateFlow<OrientReadCardState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sportiduinoHelper.subscribeToReadCard { text ->
                _state.update { it.copy(text = text) }
            }
        }
    }

}