package com.listapplication.views.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.listapplication.utils.FragmentUtil
import com.listapplication.utils.FragmentUtil.Companion.FRAG_ADD
import com.listapplication.utils.FragmentUtil.Companion.FRAG_ADD_WITH_STACK
import com.listapplication.utils.FragmentUtil.Companion.FRAG_REPLACE
import com.listapplication.utils.FragmentUtil.Companion.FRAG_REPLACE_WITH_STACK
import com.listapplication.viewmodels.base.BaseViewModel
import com.listapplication.views.base.interfaces.FragmentActivityInteractor

/**
 * BaseFragment class responsible to perform common functionality of the application's fragment classes.
 * It perform binding/inflating of a view and fragment transactions, it also responsible to provide the activity interactor for different operations.
 */
abstract class BaseFragment<T: ViewDataBinding, V: BaseViewModel<*>>: Fragment() {

    private var mActivity: BaseActivity<T, V>? = null
    private var mActivityInteractor: FragmentActivityInteractor? = null
    private lateinit var rootView: View
    private lateinit var viewBinding: T
    private lateinit var viewModel:V

    abstract fun onBackPressed(): Boolean
    abstract fun getBindingVariable(): Int
    abstract fun getViewModel(): V
    abstract fun getLayoutId(): Int

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivityInteractor = context as FragmentActivityInteractor
        if(context is BaseActivity<*,*>) {
            mActivity = context as BaseActivity<T, V>?
        }
    }

    override fun onDetach() {
        mActivity = null
        mActivityInteractor = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        rootView = viewBinding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(getBindingVariable() > 0){
            viewBinding.setVariable(getBindingVariable(), viewModel)
            viewBinding.executePendingBindings()
        }
    }

    /**
     * Function to get the base activity instance.
     * @return BaseActivity
     */
    fun getBaseActivity(): BaseActivity<T, V>? = mActivity

    /**
     * Function to get the activity interactor.
     * @return FragmentActivityInteractor.
     */
    fun getActivityInteractor(): FragmentActivityInteractor? = mActivityInteractor

    /**
     * Function to get the view binding instance.
     * @return ViewDataBinding.
     */
    fun getViewDataBinding(): ViewDataBinding = viewBinding

    /**
     * Function to show the toast message.
     * @param message : Message to display.
     */
    fun showMessage(message : String){
        mActivity?.showMessage(message)
    }

    /**
     * Function to show the snackbar message.
     * @param message : Message to display on snackbar.
     */
    fun onError(message: String?) {
        mActivity?.onError(message)
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
        childFragmentManager.popBackStackImmediate()
    }

    /**
     * Function to get the fragment by tag identifier.
     * @param fragmentType: Fragment constant to get the fragment tag.
     * @return Fragment: Returns fragment if present else null.
     **/
    fun getFragmentFromBackStack(fragmentType: Int): Fragment? {
        return childFragmentManager.findFragmentByTag(FragmentUtil.getFragmentTag(fragmentType))
    }

    private fun addFragment(bundle: Bundle?, fragmentType: Int, transType: Int, frameId: Int) {
        if (!mActivity!!.isFinishing) {
            val fm = childFragmentManager
            val ft = fm.beginTransaction()
            var fragment: Fragment? = null

            if (fragment == null) {
                fragment = instantiate(
                    this.context,
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

}