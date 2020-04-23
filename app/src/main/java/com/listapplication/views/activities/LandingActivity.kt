package com.listapplication.views.activities

import android.os.Bundle
import com.listapplication.BR
import com.listapplication.CustomLogs
import com.listapplication.R
import com.listapplication.databinding.ActivityLandingBinding
import com.listapplication.utils.FragmentUtil
import com.listapplication.utils.FragmentUtil.Companion.FRAGMENT_ERROR_SCREEN
import com.listapplication.utils.FragmentUtil.Companion.FRAGMENT_REPOSITORY_SCREEN
import com.listapplication.utils.FragmentUtil.Companion.FRAG_REPLACE
import com.listapplication.viewmodels.LandingViewModel
import com.listapplication.viewmodels.interfaces.LandingNavigator
import com.listapplication.views.base.BaseActivity
import com.listapplication.views.base.interfaces.FragmentActivityInteractor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * LandingActivity is the launcher activity to initiate the data loading.
 * It is responsible to trigger different fragments based on different criteria triggers.
 * Currently launches repo and error fragments.
 */
class LandingActivity : BaseActivity<ActivityLandingBinding, LandingViewModel>(), LandingNavigator, FragmentActivityInteractor {

    private val mCustomLogs: CustomLogs by inject()

    private val mViewModel: LandingViewModel by viewModel()

    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_landing

    override fun getViewModelFromChildActivity(): LandingViewModel = mViewModel

    override fun init(savedInstanceState: Bundle?) {
        mCustomLogs("Activity init triggered")
        mViewModel.setContract(this)
        if(savedInstanceState == null) {
            mViewModel.getShimmerLoading().set(true)
            mViewModel.fetchTrendingRepositories(false)
        }
    }

    override fun setFragment(bundle: Bundle?, fragmentType: Int, transType: Int) {
        setCurrentFragment(bundle, fragmentType, transType, R.id.fragment_container)
    }

    override fun launchRepoScreen() {
        if(supportFragmentManager.findFragmentByTag(FragmentUtil.getFragmentTag(
                FRAGMENT_REPOSITORY_SCREEN)) == null) {
            setFragment(null, FRAGMENT_REPOSITORY_SCREEN, FRAG_REPLACE)
        }
    }

    override fun launchErrorScreen() {
        if(supportFragmentManager.findFragmentByTag(FragmentUtil.getFragmentTag(
                FRAGMENT_ERROR_SCREEN)) == null) {
            setFragment(null, FRAGMENT_ERROR_SCREEN, FRAG_REPLACE)
        }
    }
}
