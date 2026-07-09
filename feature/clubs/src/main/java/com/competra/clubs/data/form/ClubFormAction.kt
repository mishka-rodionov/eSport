package com.competra.clubs.data.form

import com.competra.ui.BaseAction

sealed class ClubFormAction : BaseAction {
    data class NameChanged(val name: String) : ClubFormAction()
    data class DescriptionChanged(val description: String) : ClubFormAction()
    data class AllowJoinRequestsChanged(val allow: Boolean) : ClubFormAction()
    data object Save : ClubFormAction()
}
