package com.competra.clubs.data.form

import com.competra.ui.BaseState

data class ClubFormState(
    val name: String = "",
    val description: String = "",
    val allowJoinRequests: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: Boolean = false
) : BaseState
