package com.rodionov.domain.repository.auth

import com.rodionov.domain.models.user.User

interface AuthRepository {

    suspend fun login(email: String): Result<Any>

    suspend fun sendAuthCode(email: String, code: String): Result<User>

    suspend fun register(firstName: String, lastName: String, bdate: String, email: String): Result<Any>

}