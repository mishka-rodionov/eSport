package com.rodionov.resources.di

import com.rodionov.resources.ResourceProvider
import com.rodionov.resources.ResourceProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val resourceModule = module {
    singleOf(::ResourceProviderImpl) bind ResourceProvider::class
}