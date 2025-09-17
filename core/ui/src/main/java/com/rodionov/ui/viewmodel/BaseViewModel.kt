package com.rodionov.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodionov.ui.BaseAction

import com.rodionov.ui.BaseEffect
import com.rodionov.ui.BaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : BaseState>(initState: State): ViewModel() {

    private val _state = MutableStateFlow(initState)
    val state = _state.asStateFlow()

        protected fun updateState(info: suspend State.() -> State) {
        viewModelScope.launch(Dispatchers.Main.immediate) { _state.update { info.invoke(it) } }
    }

    abstract fun onAction(action: BaseAction)

}