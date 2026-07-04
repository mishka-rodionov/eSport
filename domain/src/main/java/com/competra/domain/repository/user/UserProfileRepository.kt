package com.competra.domain.repository.user

import com.competra.domain.models.CropRect
import com.competra.domain.models.user.User

interface UserProfileRepository {

    suspend fun updateAvatarUrl(avatarUrl: String, cropRect: CropRect? = null): Result<User>

    suspend fun getProfile(): Result<User>
}
