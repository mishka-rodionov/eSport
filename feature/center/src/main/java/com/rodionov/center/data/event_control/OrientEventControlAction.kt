package com.rodionov.center.data.event_control

sealed class OrientEventControlAction {

    data object OpenOrientReadCard: OrientEventControlAction()

}