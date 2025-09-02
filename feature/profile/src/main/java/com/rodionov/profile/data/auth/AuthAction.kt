package com.rodionov.profile.data.auth

sealed class AuthAction {

    data class AuthClicked(val email: String): AuthAction()
    data class AuthCodeEntered(val code: String): AuthAction()
}