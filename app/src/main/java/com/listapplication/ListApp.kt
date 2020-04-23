package com.listapplication

import android.app.Application
import androidx.multidex.MultiDex
import com.listapplication.injections.Modules
import org.koin.android.ext.android.startKoin

/**
 * This app helps to avoid extra time require to build a basic architecture to start developing an android application.
 * You can use this application to start writing your code on top of this existing architecture code.
 * This application built on following component.
 * Language Used: Kotlin,
 * Architecture: MVVM (LiveData),
 * Dependency Injection: Koin,
 * Database: Room,
 * Networking: Retrofit, OkHttp,
 * Glide Image Processing
 * Documentation: Dokka,
 * Background Processing: Reactive Extension
 * Testing Coverage
 */

class ListApp: Application() {

    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        //Start Koin
        startKoin(applicationContext, Modules.instance(this).modules)
    }
}