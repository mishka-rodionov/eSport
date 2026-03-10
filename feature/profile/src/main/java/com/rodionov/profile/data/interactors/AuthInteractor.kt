package com.rodionov.profile.data.interactors

import com.rodionov.domain.models.auth.Token
import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.domain.repository.user.UserRepository

class AuthInteractor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {

    suspend fun authorize(email: String, code: String): Result<Any> {
        return authRepository.authorize(email, code).mapCatching {
            val (user, token) = it
            retrieveTokenAndSaveUser(token, user)
        }
    }

    private suspend fun retrieveTokenAndSaveUser(
        token: Token,
        user: User
    ) {
        val accessToken = token.accessToken
        val refreshToken = token.refreshToken
        if (accessToken != null && refreshToken != null) {
            tokenRepository.saveTokens(accessToken, refreshToken)
        }
        userRepository.saveUser(user)
    }

    suspend fun register(firstName: String, lastName: String, bdate: Long, email: String): Result<Any> {
        return authRepository.register(firstName, lastName, bdate, email)
    }

}