package com.listapplication.injections

import android.app.Application
import android.content.Context
import com.listapplication.CustomLogs
import com.listapplication.customLogs
import com.listapplication.utils.PreferencesUtil
import org.koin.dsl.module.module

/**
 * Koin Modules class initialize all the modules and provide all the dependent classes to the application.
 */
class Modules(
    application: Application
) {

    init {
        sInstance = this
    }

    private val mAppModule = module(override = true) {
        single<CustomLogs> { ::customLogs }
        single { application.applicationContext.getSharedPreferences(PreferencesUtil::class.java.name, Context.MODE_PRIVATE) }
        single { PreferencesUtil(get()) }
    }

    val modules = listOf(mAppModule, mNetworkModule, mViewModelModule, mRepositoryModule)

    companion object {
        private var sInstance: Modules? = null

        fun instance(application: Application) = sInstance?.let { it } ?: Modules(application)
    }
}