package com.competra.center.data.group_splits

import com.competra.domain.models.orienteering.OrienteeringDirection
import com.competra.domain.models.orienteering.SplitsTable
import com.competra.ui.BaseState

data class GroupSplitsTableState(
    val groupTitle: String = "",
    val table: SplitsTable? = null,
    val direction: OrienteeringDirection = OrienteeringDirection.FORWARD,
    val isLoading: Boolean = true,
) : BaseState
