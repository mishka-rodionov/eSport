package com.competra.center.data.group_splits

import com.competra.center.data.results.SplitsTable
import com.competra.ui.BaseState

data class GroupSplitsTableState(
    val groupTitle: String = "",
    val table: SplitsTable? = null,
    val isLoading: Boolean = true,
) : BaseState
