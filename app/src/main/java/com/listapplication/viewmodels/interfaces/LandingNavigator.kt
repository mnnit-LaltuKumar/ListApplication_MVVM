package com.listapplication.viewmodels.interfaces

/**
 * Provide contract between view model and view.
 */
interface LandingNavigator {

    fun showMessage(message: String)

    fun launchRepoScreen()

    fun launchErrorScreen()
}