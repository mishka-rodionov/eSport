package com.rodionov.profile.data.auth

sealed class AuthAction {

    data object AuthClicked: AuthAction()

}