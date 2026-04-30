package com.rodionov.remote.repository.user

import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.user.UserProfileRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.request.user.UserProfileUpdateRequest
import com.rodionov.remote.response.mappers.toDomain

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
