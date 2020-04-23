package com.listapplication.views.base

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.listapplication.R
import com.listapplication.utils.FragmentUtil
import com.listapplication.utils.FragmentUtil.Companion.FRAG_ADD
import com.listapplication.utils.FragmentUtil.Companion.FRAG_ADD_WITH_STACK
import com.listapplication.utils.FragmentUtil.Companion.FRAG_REPLACE
import com.listapplication.utils.FragmentUtil.Companion.FRAG_REPLACE_WITH_STACK
import com.listapplication.viewmodels.base.BaseViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * BaseActivity class responsible to perform common functionality of the application's activity classes.
 * It perform binding and fragment transactions, it also responsible to grant the runtime permissions and show the error messages.
 */
abstract class BaseActivity<T: ViewDataBinding, V: BaseViewModel<*>>: AppCompatActivity() {

    private lateinit var mViewBinding: T

    abstract fun getViewModelFromChildActivity() : V
    abstract fun getLayoutId(): Int
    abstract fun getBindingVariable() : Int
    abstract fun init(savedInstanceState: Bundle?)


    /**
     * @return View Binding instance.
     */
    fun getViewBinding(): T = mViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performBinding()
        init(savedInstanceState)
    }

    /**
     * Function perform view binding operation.
     */
    private fun performBinding() {
        mViewBinding = DataBindingUtil.setContentView(this, getLayoutId())
        val viewModel:V = getViewModelFromChildActivity()
        if(getBindingVariable() > 0) {
            mViewBinding.setVariable(getBindingVariable(), viewModel)
            mViewBinding.executePendingBindings()
        }
    }

    /**
     * Function to check the existing permission state.
     * @param permission : Permission to check
     * @return Boolean: Current status of permission granted or not granted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function to request the permission.
     * @param permissions : Permission to grant.
     * @param requestCode : Code to identify the permissions.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        getViewModelFromChildActivity().onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Function to show the toast message.
     * @param message : Message to display.
     */
    fun showMessage(message : String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Function to show the snackbar message.
     * @param message : Message to display on snackbar.
     */
    fun onError(message: String?) {
        message?.let { showSnackBar(it) }
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
        val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        textView.textSize = 16F
        snackbar.show()
    }

    /**
     * Function to display a fragment on the container provided.
     * @param bundle : Bundle needs to be passed to fragment.
     * @param fragmentType: Fragment constant to instantiate the fragment.
     * @param transType: Transaction needs to be performed (ADD, REPLACE, BACKSTACK STATE).
     * @param frameId: Container where fragment transaction needs to be performed.
     */
    fun setCurrentFragment(bundle: Bundle?, fragmentType: Int, transType: Int, frameId: Int) {
        addFragment(bundle, fragmentType, transType, frameId)
    }

    /**
     * Function to remove the top fragment.
     **/
    fun popTopFragment() {
        supportFragmentManager.popBackStackImmediate()
    }

    private fun addFragment(bundle: Bundle?, fragmentType: Int, transType: Int, frameId: Int) {
        if (!isFinishing) {
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            var fragment: Fragment? = null

            if (fragment == null) {
                fragment = Fragment.instantiate(
                    this,
                    FragmentUtil.getFragmentTag(fragmentType),
                    bundle
                )
                when (transType) {
                    FRAG_ADD -> ft.add(frameId, fragment, FragmentUtil.getFragmentTag(fragmentType))
                    FRAG_REPLACE -> ft.replace(frameId, fragment, FragmentUtil.getFragmentTag(fragmentType))
                    FRAG_REPLACE_WITH_STACK -> {
                        ft.replace(frameId, fragment, FragmentUtil.getFragmentTag(fragmentType))
                        ft.addToBackStack(FragmentUtil.getFragmentTag(fragmentType))
                    }
                    FRAG_ADD_WITH_STACK -> {
                        ft.add(frameId, fragment, FragmentUtil.getFragmentTag(fragmentType))
                        ft.addToBackStack(FragmentUtil.getFragmentTag(fragmentType))
                    }
                }
            } else {
                ft.attach(fragment)
            }
            ft.commitAllowingStateLoss()
            fm.executePendingTransactions()
        }
    }

    /**
     * Function to get the fragment by tag identifier.
     * @param fragmentType: Fragment constant to get the fragment tag.
     * @return Fragment: Returns fragment if present else null.
     **/
    fun getFragmentFromBackStack(fragmentType: Int): Fragment? {
        return supportFragmentManager.findFragmentByTag(FragmentUtil.getFragmentTag(fragmentType))
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0) {
            val backIndex = supportFragmentManager.backStackEntryCount - 1
            val backEntry = supportFragmentManager.getBackStackEntryAt(backIndex)
            val fragTag = backEntry.name
            val fragment = supportFragmentManager.findFragmentByTag(fragTag) as BaseFragment<T, V>?
            fragment?.let {
                val isFragmentBackHandled = fragment.onBackPressed()
                if(isFragmentBackHandled) return
            }
            popTopFragment()
            return
        }
        super.onBackPressed()
    }

}