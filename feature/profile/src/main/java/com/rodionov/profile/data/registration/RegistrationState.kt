package com.rodionov.profile.data.registration

import com.rodionov.ui.BaseState

data class RegistrationState(
    var email: String = "mishka727@yandex.ru",
    var firstName: String = "Михаил",
    var lastName: String = "Родионов",
    var bdate: String = "06.04.1989"
): BaseState
