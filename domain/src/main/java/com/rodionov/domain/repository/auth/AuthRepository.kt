package com.rodionov.domain.repository.auth

interface AuthRepository {

    suspend fun requestAuthCode(email: String): Result<Any>

    suspend fun sendAuthCode(email: String, code: String): Result<Any>

}