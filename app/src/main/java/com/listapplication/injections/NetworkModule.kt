package com.listapplication.injections

import com.listapplication.BuildConfig
import com.listapplication.repositories.network.NetworkInterface
import com.listapplication.utils.ConstantUtil.Companion.DEFAULT_REQUEST_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val mNetworkModule = module {
    single { provideHttpLoggingInterceptor() }
    single { provideDefaultOkHttpClient(get()) }
    single { provideRetrofit(get()) }
}

fun provideDefaultOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .connectTimeout(DEFAULT_REQUEST_TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(DEFAULT_REQUEST_TIMEOUT, TimeUnit.SECONDS)
    .writeTimeout(DEFAULT_REQUEST_TIMEOUT, TimeUnit.SECONDS)
    .build()

fun provideRetrofit(client: OkHttpClient): NetworkInterface = Retrofit.Builder()
    .baseUrl(BuildConfig.SERVER_BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()
    .create(NetworkInterface::class.java)

fun provideHttpLoggingInterceptor() : HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
}