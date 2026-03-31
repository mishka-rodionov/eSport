package com.rodionov.center.data.draw

import com.rodionov.ui.BaseAction

sealed class DrawAction : BaseAction {
    data object StartDrawOperation : DrawAction()
    data object StartGroupDrawOperation : DrawAction()
}
