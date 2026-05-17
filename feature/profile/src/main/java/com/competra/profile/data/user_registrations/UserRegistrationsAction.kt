package com.competra.profile.data.user_registrations

import com.competra.ui.BaseAction

/** Действия на экране «Предстоящие старты». */
sealed interface UserRegistrationsAction : BaseAction {
    data class OnItemClick(val eventId: Long) : UserRegistrationsAction
    data object OnBackClick : UserRegistrationsAction
}
