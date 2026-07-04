package com.competra.eventdetails.presentation.group_splits

import com.competra.domain.models.orienteering.SplitsTable
import com.competra.ui.BaseState

data class EventGroupSplitsTableState(
    val groupTitle: String = "",
    val table: SplitsTable? = null,
    val isLoading: Boolean = true,
) : BaseState
