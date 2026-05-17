package com.competra.profile.data.user_registrations

import com.competra.domain.models.orienteering.OrienteeringCompetition
import com.competra.ui.BaseState

/**
 * Состояние экрана «Предстоящие старты» — список соревнований, на которые
 * текущий пользователь зарегистрирован участником.
 */
data class UserRegistrationsState(
    val isLoading: Boolean = false,
    val registrations: List<OrienteeringCompetition> = emptyList()
) : BaseState
