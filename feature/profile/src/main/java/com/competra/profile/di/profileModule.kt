package com.competra.profile.di

import com.competra.profile.data.interactors.AuthInteractor
import com.competra.profile.presentation.auth.AuthViewModel
import com.competra.profile.presentation.auth_code.AuthCodeViewModel
import com.competra.profile.presentation.main_profile.ProfileViewModel
import com.competra.profile.presentation.profile_editor.ProfileEditorViewModel
import com.competra.profile.presentation.push_preferences.PushPreferencesViewModel
import com.competra.profile.presentation.registration.RegistrationViewModel
import com.competra.profile.presentation.user_registrations.UserRegistrationsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::AuthCodeViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::ProfileEditorViewModel)
    viewModelOf(::UserRegistrationsViewModel)
    viewModelOf(::PushPreferencesViewModel)
    factoryOf(::AuthInteractor)
}