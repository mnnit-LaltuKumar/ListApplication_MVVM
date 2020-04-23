package com.listapplication.injections

import com.listapplication.viewmodels.LandingViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val mViewModelModule = module {
    viewModel { LandingViewModel(get(), get("repoDataImpl"), get("repoDB"), get(), get()) }
}