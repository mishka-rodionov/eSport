package com.rodionov.center.data.event_control

import com.rodionov.ui.BaseAction

/**
 * Действия на экране управления соревнованием по ориентированию.
 */
sealed class OrientEventControlAction: BaseAction {

    data object OpenOrientReadCard: OrientEventControlAction()
    data object OpenParticipantLists: OrientEventControlAction()
    data object OpenDrawParticipants: OrientEventControlAction()
    data object OpenResults: OrientEventControlAction()
    data object OpenGetOrienteeringChip: OrientEventControlAction()
    data object StartCompetition: OrientEventControlAction()

}