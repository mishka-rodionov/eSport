package com.rodionov.profile.data.registration

import com.rodionov.ui.BaseAction

sealed class RegistrationAction: BaseAction {

    data object RegisterUser: RegistrationAction()

    data class UpdateEmail(val email: String): RegistrationAction()
    data class UpdateFirstName(val firstName: String): RegistrationAction()
    data class UpdateLastName(val lastName: String): RegistrationAction()
    data class UpdateBdate(val bdate: String): RegistrationAction()

}