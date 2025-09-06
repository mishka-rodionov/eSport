package com.rodionov.remote.network.retrofit

import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.request.auth.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenRepository: TokenRepository,
    private val authRemoteDataSource: AuthRemoteDataSource
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Достаём refreshToken
        val refreshToken = tokenRepository.getRefreshToken() ?: return null

        return authRemoteDataSource.refreshToken(
                RefreshRequest(refreshToken)
            ).mapCatching {
                val body = it.result?.token
                if (body != null) {
                    // сохраняем новые токены
                    tokenRepository.saveTokens(
                        access = body.accessToken,
                        refresh = body.refreshToken
                    )

                    // возвращаем новый запрос с обновлённым accessToken
                    response.request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer ${body.accessToken}")
                        .build()
                } else {
                    null
                }
            }
    }
}