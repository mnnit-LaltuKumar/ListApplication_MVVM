package com.listapplication.utils

import androidx.fragment.app.Fragment
import com.listapplication.views.fragments.ErrorFragment
import com.listapplication.views.fragments.RepoFragment

/**
 * Helper class to hold all the Fragment operation constant.
 */
class FragmentUtil {

    companion object {
        const val FRAG_ADD = 1
        const val FRAG_REPLACE = 2
        const val FRAG_REPLACE_WITH_STACK = 3
        const val FRAG_ADD_WITH_STACK = 4

        fun getFragmentTag(type: Int): String {
            return when (type) {
                FRAGMENT_REPOSITORY_SCREEN -> RepoFragment::class.java.name
                FRAGMENT_ERROR_SCREEN -> ErrorFragment::class.java.name
                else -> Fragment::class.java.name
            }
        }

        const val FRAGMENT_REPOSITORY_SCREEN = 100
        const val FRAGMENT_ERROR_SCREEN = 101
    }
}