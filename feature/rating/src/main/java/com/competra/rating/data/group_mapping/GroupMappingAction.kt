package com.competra.rating.data.group_mapping

import com.competra.ui.BaseAction

sealed class GroupMappingAction : BaseAction {
    data object BackClick : GroupMappingAction()
    data class MappingChanged(val participantGroupId: Long, val ratingGroupId: Long?) : GroupMappingAction()
    data object Save : GroupMappingAction()
}
