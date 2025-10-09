package com.rodionov.profile.data.auth

import com.rodionov.ui.BaseAction

sealed class AuthAction: BaseAction {

    data class AuthClicked(val email: String): AuthAction()
    data class AuthCodeEntered(val code: String): AuthAction()
}