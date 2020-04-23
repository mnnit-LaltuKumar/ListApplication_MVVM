package com.listapplication.views.fragments

import android.os.Bundle
import androidx.databinding.library.baseAdapters.BR
import com.listapplication.R
import com.listapplication.databinding.FragmentErrorBinding
import com.listapplication.viewmodels.LandingViewModel
import com.listapplication.views.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * ErrorFragment shows the error message and retry option to end user.
 */
class ErrorFragment: BaseFragment<FragmentErrorBinding, LandingViewModel>() {

    private val mViewModel: LandingViewModel by sharedViewModel()

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_error

    override fun getViewModel(): LandingViewModel = mViewModel

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

}