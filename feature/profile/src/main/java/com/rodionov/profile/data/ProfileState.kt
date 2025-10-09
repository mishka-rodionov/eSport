package com.rodionov.profile.data

import com.rodionov.domain.models.user.User
import com.rodionov.ui.BaseState

data class ProfileState(
    val user: User? = null
) : BaseState