package com.rodionov.profile.di

import com.rodionov.profile.presentation.auth.AuthViewModel
import com.rodionov.profile.presentation.auth_code.AuthCodeViewModel
import com.rodionov.profile.presentation.main_profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::AuthCodeViewModel)
}