package com.listapplication.viewmodels.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference

/**
 * BaseViewModel class responsible to perform common functionality of the view model classes.
 * It process the contract as weak reference and perform cleanup of data when a view model destroys.
 */
abstract class BaseViewModel<N>: ViewModel() {

    private lateinit var mContract: WeakReference<N>
    protected val mCompositeDisposable = CompositeDisposable()

    abstract fun processPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    override fun onCleared() {
        mCompositeDisposable.dispose()
        mContract.clear()
        super.onCleared()
    }

    /**
     * Function to get the Contract for the current view model.
     * @return ViewModelContract
     */
    protected fun getContract(): N = mContract.get()!!

    /**
     * Function to set the contract for the current view model.
     * @param contract Current view model contract.
     */
    fun setContract(contract: N) {
        mContract = WeakReference(contract)
    }

    /**
     * Function use to decide the flow of business logic when permission granted or declined.
     * This function will get call back when a permission grants or declined.
     * @param requestCode: Uniquely identify the request.
     * @param permissions: Permissions to be requested.
     * @param grantResults: Current state of the requested permissions.
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        processPermissionResult(requestCode, permissions, grantResults)
    }
}