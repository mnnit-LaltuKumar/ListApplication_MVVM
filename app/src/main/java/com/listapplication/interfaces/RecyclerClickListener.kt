package com.listapplication.interfaces

import com.listapplication.repositories.models.Repository

/**
 * Interface to support click event for Recycler View.
 */
interface RecyclerClickListener {
    fun itemClicked(repository: Repository, position: Int)
}