package com.competra.remote.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.competra.domain.models.KindOfSport
import com.competra.domain.repository.auth.TokenRepository
import com.competra.remote.datasource.auth.AuthRemoteDataSource
import com.competra.remote.interceptors.MockInterceptor
import com.competra.remote.network.adapters.KindOfSportAdapter
import com.competra.remote.network.interceptors.AuthInterceptor
import com.competra.remote.network.retrofit.ResultCallAdapterFactory
import com.competra.remote.network.retrofit.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 60

val retrofitModule = module {
    singleOf(::createGson)
    singleOf(::retrofit)
}

fun retrofit(
    gson: Gson,
    tokenRepository: TokenRepository,
    context: Context
): Retrofit {
    val builder = OkHttpClient.Builder()
    val collector = ChuckerCollector(context, true)
    val interceptor = ChuckerInterceptor
        .Builder(context)
        .collector(collector)
        .build()
    builder.addInterceptor(interceptor)
    // AuthInterceptor должен идти ДО HttpLoggingInterceptor, иначе в логе не видно
    // подставленный Authorization-заголовок (logger срабатывает раньше auth-интерсептора).
    val okClient = builder
        .addInterceptor(AuthInterceptor(tokenRepository = tokenRepository))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .authenticator(TokenAuthenticator(tokenRepository = tokenRepository))
//        .addInterceptor(MockInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .build()
    val baseUrl = "https://BASE_URL"
//    val localBaseUrl = "http://192.168.1.113:8080/"
    val localBaseUrl = "https://competra.ru/api/" // remote server
//    val localBaseUrl = "http://188.68.223.12:8080/" // remote server
//    val localBaseUrl = "http://192.168.1.71:8080/"
    return Retrofit.Builder()
        .baseUrl(localBaseUrl)
//        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .client(okClient)
        .build()
}

private fun createGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(KindOfSport::class.java, KindOfSportAdapter())
        .create()
}