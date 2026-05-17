package com.competra.remote.repository.user

import com.competra.domain.models.user.User
import com.competra.domain.repository.user.UserProfileRepository
import com.competra.remote.datasource.auth.AuthRemoteDataSource
import com.competra.remote.request.user.UserProfileUpdateRequest
import com.competra.remote.response.mappers.toDomain

class UserProfileRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource
) : UserProfileRepository {

    override suspend fun updateAvatarUrl(avatarUrl: String): Result<User> {
        return authRemoteDataSource.updateProfile(UserProfileUpdateRequest(avatarUrl = avatarUrl))
            .mapCatching { it.result!!.toDomain() }
    }

    override suspend fun getProfile(): Result<User> {
        return authRemoteDataSource.getProfile()
            .mapCatching { it.result!!.toDomain() }
    }
}
