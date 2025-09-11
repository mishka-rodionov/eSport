package com.rodionov.profile.data

sealed class ProfileAction {

    data object ToAuth: ProfileAction()
    data object ToRegister: ProfileAction()

}