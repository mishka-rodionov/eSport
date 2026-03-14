package com.rodionov.profile.data

/**
 * Действия пользователя на экране профиля.
 */
sealed class ProfileAction {

    /** Переход к экрану авторизации. */
    data object ToAuth: ProfileAction()
    
    /** Переход к экрану регистрации. */
    data object ToRegister: ProfileAction()

    /** Переход к экрану редактирования профиля. */
    data object ToProfileEditor: ProfileAction()

}
