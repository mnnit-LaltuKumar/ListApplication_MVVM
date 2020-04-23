package com.listapplication.views.base.interfaces

import android.os.Bundle

/**
 * Interface provide contract between fragment and activity.
 */
interface FragmentActivityInteractor {

    fun setFragment(bundle: Bundle?, fragmentType: Int, transType: Int)

    fun popTopFragment()

}