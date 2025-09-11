package com.rodionov.profile.data.auth

import com.rodionov.domain.models.user.User

data class ProfileState(
    val user: User? = null
)