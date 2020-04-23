package com.listapplication.viewmodels

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.listapplication.CustomLogs
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import com.listapplication.utils.NetworkUtil
import com.listapplication.utils.PreferencesUtil
import com.listapplication.viewmodels.base.BaseViewModel
import com.listapplication.viewmodels.interfaces.LandingNavigator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Calendar

/**
 * ViewModel class to handle business logic and perform common operations.
 * This class is responsible to start shimmer animation, initiate network and db calls, handle pull to refresh and storing and fetching values from preferences.
 * Also, we create a common recycler adapter here to retain the instance of the adapter on orientation change.
 * The instance of this class is shared by Koin across the application.
 */
class LandingViewModel(
    private val mContext: Context,
    private val mRepoImpl: DataSource<Repository>,
    private val mDBSource: DataSource<Repository>,
    private val mPreferencesUtil: PreferencesUtil,
    private val mCustomLogs: CustomLogs
) : BaseViewModel<LandingNavigator>() {

    private val mRepoListLiveData: MutableLiveData<List<Repository>> = MutableLiveData()

    private val isLoading: ObservableBoolean = ObservableBoolean()

    private val isShimmerLoading: ObservableBoolean = ObservableBoolean()

    /**
     * Permission callback
     */
    override fun processPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
    }

    /**
     * Initiate network and db calls to get the trending repositories data.
     * Data source will take care of deleting and updating the cache.
     * @param isForceFetch set to true if data needs to be fetched from server else data read from local cache.
     * @param isPullToRefresh set to true when perform pull to refresh else false.
     * Update the view via contract.
     */
    internal fun fetchTrendingRepositories(isForceFetch: Boolean, isPullToRefresh: Boolean = false) {
        if (!isPullToRefresh) {
            isShimmerLoading.set(true)
        }
        mCompositeDisposable.add(
            preConditionCheck(isForceFetch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mCustomLogs("Success:: ${it.size}")
                    if (it.isNotEmpty()) {
                        mRepoListLiveData.value = it
                        processResponse(true)
                    } else {
                        if (!NetworkUtil.isNetworkAvailable(mContext)) {
                            processResponse(false)
                        }
                    }
                }, {
                    mCustomLogs("Server Error:: $it")
                    processResponse(false)
                })
        )
    }

    private fun processResponse(isSuccess: Boolean) {
        isLoading.set(false)
        isShimmerLoading.set(false)
        if (isSuccess) {
            getContract().launchRepoScreen()
        } else {
            getContract().launchErrorScreen()
        }
    }

    internal fun preConditionCheck(isForceFetch: Boolean): Flowable<List<Repository>> {
        return if (isForceFetch || mPreferencesUtil.getResponseTimeout() < Calendar.getInstance().timeInMillis) {
            mCustomLogs("Start fetching data from server")
            mRepoImpl.getAll()
        } else {
            mCustomLogs("Start fetching data from db")
            mDBSource.getAll()
        }
    }

    /**
     * LiveData register by the view to get the update when any changes occurred.
     * @return LiveData<List<Repository>>
     */
    fun getRepoLiveData(): LiveData<List<Repository>> {
        return mRepoListLiveData
    }

    /**
     * Use by data binding to change the view state (Pull to Refresh View).
     * @return ObservableBoolean
     */
    fun getLoadingState(): ObservableBoolean {
        return isLoading
    }

    /**
     * Use by data binding to change the view state (Shimmer Animation).
     * @return ObservableBoolean
     */
    fun getShimmerLoading(): ObservableBoolean {
        return isShimmerLoading
    }

    /**
     * Use by pull to refresh data binding to perform onRefresh Event.
     */
    fun forceRefreshData() {
        isLoading.set(true)
        fetchTrendingRepositories(isForceFetch = true, isPullToRefresh = true)
    }

    /**
     * Use by retry option.
     */
    fun forceRetryData() {
        fetchTrendingRepositories(isForceFetch = true, isPullToRefresh = false)
    }
}