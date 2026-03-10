package com.rodionov.profile.data.registration

import com.rodionov.ui.BaseState
import com.rodionov.utils.DateTimeFormat

data class RegistrationState(
    var email: String = "mishka727@yandex.ru",
    var firstName: String = "Михаил",
    var lastName: String = "Родионов",
    var bdate: Long = DateTimeFormat.transformApiDateToLong("06.04.1989")
): BaseState
