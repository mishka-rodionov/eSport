package com.rodionov.remote.extension

import org.koin.core.module.Module
import org.koin.core.scope.Scope
import retrofit2.Retrofit

fun Scope.getRetrofit(): Retrofit {
    return get<Retrofit>()
}

inline fun <reified T> Scope.createRetrofitService(clazz: Class<T>): T {
    return getRetrofit().create(clazz)
}

inline fun <reified T> Module.singleRemoteDataSourceOf(clazz: Class<T>) {
    single { createRetrofitService(clazz) }
}