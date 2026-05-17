package com.competra.profile.data.registration

import com.competra.ui.BaseState
import com.competra.utils.DateTimeFormat

data class RegistrationState(
    var email: String = "mishka727@yandex.ru",
    var firstName: String = "Михаил",
    var lastName: String = "Родионов",
    var bdate: Long = DateTimeFormat.transformApiDateToLong("06.04.1989")
): BaseState
