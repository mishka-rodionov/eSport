package com.rodionov.center.data.event_control

import com.rodionov.ui.BaseAction

sealed class OrientEventControlAction: BaseAction {

    data object OpenOrientReadCard: OrientEventControlAction()

    data object OpenParticipantLists: OrientEventControlAction()

}