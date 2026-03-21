package com.rodionov.profile.di

import com.rodionov.profile.data.interactors.AuthInteractor
import com.rodionov.profile.presentation.auth.AuthViewModel
import com.rodionov.profile.presentation.auth_code.AuthCodeViewModel
import com.rodionov.profile.presentation.main_profile.ProfileViewModel
import com.rodionov.profile.presentation.profile_editor.ProfileEditorViewModel
import com.rodionov.profile.presentation.registration.RegistrationViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::AuthCodeViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::ProfileEditorViewModel)
    factoryOf(::AuthInteractor)
}