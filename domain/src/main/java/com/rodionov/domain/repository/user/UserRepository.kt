package com.rodionov.domain.repository.user

import com.rodionov.domain.models.user.User

interface UserRepository {

    suspend fun saveUser(user: User): Result<Any>

    suspend fun retrieveUser(): Result<User>

    suspend fun isAuthorized(): Boolean

    suspend fun clearUser()

}