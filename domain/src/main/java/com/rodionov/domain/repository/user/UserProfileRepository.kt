package com.rodionov.domain.repository.user

import com.rodionov.domain.models.user.User

interface UserProfileRepository {

    suspend fun updateAvatarUrl(avatarUrl: String): Result<User>

    suspend fun getProfile(): Result<User>
}
