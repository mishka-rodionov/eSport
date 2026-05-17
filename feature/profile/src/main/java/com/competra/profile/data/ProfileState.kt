package com.competra.profile.data

import com.competra.domain.models.user.User
import com.competra.ui.BaseState

data class ProfileState(
    val user: User? = null
) : BaseState