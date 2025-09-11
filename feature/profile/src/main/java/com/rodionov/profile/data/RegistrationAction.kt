package com.rodionov.profile.data

sealed class RegistrationAction {

    data class RegisterUser(
        val firstName: String,
        val lastName: String,
        val bdate: String,
        val email: String
    ): RegistrationAction()

}