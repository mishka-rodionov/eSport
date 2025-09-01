package com.rodionov.remote.request.auth

data class AuthCodeRequest(
    val email: String,
    val code: String
)
