package com.competra.remote.network.retrofit

import com.competra.domain.repository.auth.TokenRepository
import com.competra.remote.datasource.auth.AuthRemoteDataSource
import com.competra.remote.request.auth.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.java.KoinJavaComponent

class TokenAuthenticator(
    private val tokenRepository: TokenRepository,
) : Authenticator {

    private val authRemoteDataSource: AuthRemoteDataSource by KoinJavaComponent.inject(AuthRemoteDataSource::class.java)

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Обрываем повторные попытки на одном и том же запросе, чтобы не зациклить рефреш.
        if (responseCount(response) >= MAX_RETRIES) return null

        val staleAccess = response.request.header(HEADER_AUTHORIZATION)
            ?.removePrefix(BEARER_PREFIX)

        return runBlocking {
            // Быстрый путь без лока: если другой поток уже обновил токен, просто пересобираем запрос.
            tokenRepository.getAccessToken()?.let { current ->
                if (current.isNotEmpty() && current != staleAccess) {
                    return@runBlocking response.request.withBearer(current)
                }
            }

            refreshMutex.withLock {
                // Double-check под локом: пока ждали мьютекс, сосед мог уже сделать рефреш.
                tokenRepository.getAccessToken()?.let { current ->
                    if (current.isNotEmpty() && current != staleAccess) {
                        return@withLock response.request.withBearer(current)
                    }
                }

                val refreshToken = tokenRepository.getRefreshToken() ?: return@withLock null

                val answer = authRemoteDataSource.refreshToken(
                    RefreshRequest(refreshToken)
                ).getOrNull() ?: return@withLock null

                val body = answer.result?.token ?: return@withLock null

                tokenRepository.saveTokens(
                    access = body.accessToken,
                    refresh = body.refreshToken
                )

                response.request.withBearer(body.accessToken)
            }
        }
    }

    private fun Request.withBearer(token: String): Request =
        newBuilder()
            .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
            .build()

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_RETRIES = 2
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}