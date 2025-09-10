package com.rodionov.remote.network.retrofit

import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.request.auth.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.java.KoinJavaComponent

class TokenAuthenticator(
    private val tokenRepository: TokenRepository,
) : Authenticator {

    private val authRemoteDataSource: AuthRemoteDataSource by KoinJavaComponent.inject(AuthRemoteDataSource::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        // Достаём refreshToken
        return runBlocking {
            val refreshToken = tokenRepository.getRefreshToken() ?: return@runBlocking null

            val answer = authRemoteDataSource.refreshToken(
                RefreshRequest(refreshToken)
            ).getOrNull() ?: return@runBlocking null

            val body = answer.result?.token
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