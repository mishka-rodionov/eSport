package com.competra.resources.di

import com.competra.resources.ResourceProvider
import com.competra.resources.ResourceProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val resourceModule = module {
    singleOf(::ResourceProviderImpl) bind ResourceProvider::class
}