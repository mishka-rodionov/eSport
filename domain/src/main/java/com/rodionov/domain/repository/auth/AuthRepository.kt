package com.rodionov.domain.repository.auth

import com.rodionov.domain.models.auth.Token

interface AuthRepository {

    suspend fun requestAuthCode(email: String): Result<Any>

    suspend fun sendAuthCode(email: String, code: String): Result<Token>

}