package com.rodionov.profile.data.user_registrations

import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.ui.BaseState

/**
 * Состояние экрана «Предстоящие старты» — список соревнований, на которые
 * текущий пользователь зарегистрирован участником.
 */
data class UserRegistrationsState(
    val isLoading: Boolean = false,
    val registrations: List<OrienteeringCompetition> = emptyList()
) : BaseState
