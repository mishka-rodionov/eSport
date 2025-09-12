package com.rodionov.remote.repository.auth

import android.util.Log
import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.request.auth.AuthCodeRequest
import com.rodionov.remote.request.auth.EmailRequest
import com.rodionov.remote.request.user.UserRequest
import com.rodionov.remote.response.mappers.toDomain
import com.rodionov.remote.response.mappers.toRequest

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenRepository: TokenRepository
) : AuthRepository {

    override suspend fun login(email: String): Result<Any> {
        return authRemoteDataSource.login(EmailRequest(email))
    }

    override suspend fun sendAuthCode(email: String, code: String): Result<User> {
        return authRemoteDataSource.sendAuthCode(AuthCodeRequest(email, code)).onSuccess {
            Log.d("LOG_TAG", "sendAuthCode: ${it}")
            it.result?.let { authResponse ->
                tokenRepository.saveTokens(
                    authResponse.token.accessToken,
                    authResponse.token.refreshToken
                )
            }
        }.onFailure {
            Log.d("LOG_TAG", "sendAuthCode: $it")
        }.mapCatching { it.result!!.user.toDomain() }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        bdate: String,
        email: String
    ): Result<Any> {
        return authRemoteDataSource.register(
            UserRequest(
                firstName = firstName,
                lastName = lastName,
                birthDate = bdate,
                email = email
            )
        )
    }
}