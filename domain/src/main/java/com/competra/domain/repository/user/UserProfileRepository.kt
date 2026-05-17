package com.competra.domain.repository.user

import com.competra.domain.models.user.User

interface UserProfileRepository {

    suspend fun updateAvatarUrl(avatarUrl: String): Result<User>

    suspend fun getProfile(): Result<User>
}
