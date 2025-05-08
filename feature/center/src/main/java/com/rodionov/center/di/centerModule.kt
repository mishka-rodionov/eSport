package com.rodionov.center.di

import com.rodionov.center.presentation.main.CenterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val centerModule = module {
    viewModelOf(::CenterViewModel)
}