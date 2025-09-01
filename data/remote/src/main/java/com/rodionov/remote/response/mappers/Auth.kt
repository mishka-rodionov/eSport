package com.rodionov.remote.response.mappers

import com.rodionov.domain.models.auth.Token
import com.rodionov.remote.response.auth.TokenResponse

fun TokenResponse.toDomain(): Token {
    return Token(accessToken = accessToken, refreshToken = refreshToken)
}