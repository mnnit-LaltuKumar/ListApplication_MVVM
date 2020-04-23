package com.listapplication.views.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.listapplication.BR
import com.listapplication.CustomLogs
import com.listapplication.R
import com.listapplication.databinding.FragmentRepoBinding
import com.listapplication.utils.DividerItemDecoration
import com.listapplication.viewmodels.LandingViewModel
import com.listapplication.views.adapters.RepoRecyclerAdapter
import com.listapplication.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_repo.recycler_view
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * RepoFragment shows the recycler view with the repo data.
 * It also listen for the repo data change event via LiveData observer.
 */
class RepoFragment: BaseFragment<FragmentRepoBinding, LandingViewModel>() {

    private val mCustomLogs: CustomLogs by inject()

    private val mViewModel: LandingViewModel by sharedViewModel()

    private var mAdapter: RepoRecyclerAdapter? = null

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_repo

    override fun getViewModel(): LandingViewModel = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCustomLogs("Fragment:: onCreate")
        retainInstance = true
        mAdapter = RepoRecyclerAdapter(mutableListOf(), mCustomLogs)
        initRepoData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCustomLogs("Fragment:: onViewCreated")
        mCustomLogs("Activity:: ViewModel : $mViewModel")

        initView()
    }

    private fun initView() {
        val mLayoutManager = LinearLayoutManager(activity)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.addItemDecoration(DividerItemDecoration(activity!!))
        recycler_view.adapter = mAdapter
    }

    private fun initRepoData() {
        mViewModel.getRepoLiveData()
            .observe(this, Observer { list ->
                if(list.isNotEmpty()) {
                    mCustomLogs("Fragment :: LiveData Triggered with item size :: ${list.size}")
                    mAdapter?.let {
                        it.setData(list)
                    }
                }
            })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}