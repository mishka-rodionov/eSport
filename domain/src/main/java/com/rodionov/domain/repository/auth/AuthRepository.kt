package com.rodionov.domain.repository.auth

import com.rodionov.domain.models.auth.Token
import com.rodionov.domain.models.user.User

interface AuthRepository {

    suspend fun login(email: String): Result<Any>

    suspend fun authorize(email: String, code: String): Result<Pair<User, Token>>

    suspend fun register(firstName: String, lastName: String, bdate: String, email: String): Result<Pair<User, Token>>

}