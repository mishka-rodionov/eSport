package com.rodionov.remote.repository.auth

import android.util.Log
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.request.auth.AuthCodeRequest
import com.rodionov.remote.request.auth.EmailRequest
import com.rodionov.remote.response.mappers.toDomain

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource
): AuthRepository {

    override suspend fun requestAuthCode(email: String): Result<Any> {
        return authRemoteDataSource.requestAuthCode(EmailRequest(email))
    }

    override suspend fun sendAuthCode(email: String, code: String): Result<Any> {
        return authRemoteDataSource.sendAuthCode(AuthCodeRequest(email, code)).onSuccess {
            Log.d("LOG_TAG", "sendAuthCode: ${it}")
        }.onFailure {
            Log.d("LOG_TAG", "sendAuthCode: $it")
        }.mapCatching { it.result!! }
    }
}