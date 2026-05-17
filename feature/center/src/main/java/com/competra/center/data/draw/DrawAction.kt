package com.competra.center.data.draw

import com.competra.ui.BaseAction

sealed class DrawAction : BaseAction {
    data object StartDrawOperation : DrawAction()
    data object StartGroupDrawOperation : DrawAction()
}
