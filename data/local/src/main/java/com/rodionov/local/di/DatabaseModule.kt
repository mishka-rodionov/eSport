package com.rodionov.local.di

import com.rodionov.local.UserDao
import com.rodionov.local.database.DatabaseHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    singleOf(::DatabaseHelper)
    single<UserDao> { get<DatabaseHelper>().db.userDao() }
}