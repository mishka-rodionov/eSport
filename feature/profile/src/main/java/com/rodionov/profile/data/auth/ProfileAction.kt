package com.rodionov.profile.data.auth

sealed class ProfileAction {

    data object ToAuth: ProfileAction()

}