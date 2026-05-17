package com.competra.domain.repository.user

import com.competra.domain.models.user.User

interface UserRepository {

    suspend fun saveUser(user: User): Result<Any>

    suspend fun retrieveUser(): Result<User>

    suspend fun isAuthorized(): Boolean

    suspend fun clearUser()

}