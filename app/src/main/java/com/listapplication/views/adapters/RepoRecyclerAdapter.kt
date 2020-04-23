package com.listapplication.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.listapplication.BR
import com.listapplication.CustomLogs
import com.listapplication.R
import com.listapplication.databinding.LayoutRepoItemBinding
import com.listapplication.interfaces.RecyclerClickListener
import com.listapplication.repositories.models.Repository

/**
 * RecyclerAdapter displays the data in recycler view.
 * It handles the click event and expand/collapse the view on click of an item.
 */
class RepoRecyclerAdapter(
    private var mList: List<Repository>,
    private val mCustomLogs: CustomLogs
) : RecyclerView.Adapter<RepoRecyclerAdapter.ViewHolder>(), RecyclerClickListener {

    private var currentExpand = -1
    private var previousExpand = -1

    inner class ViewHolder(val itemBinding: LayoutRepoItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(repo: Repository, pos: Int) {
            itemBinding.setVariable(BR.repository, repo)
            itemBinding.setVariable(BR.position, pos)
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = DataBindingUtil.inflate<LayoutRepoItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_repo_item,
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = mList[position]
        holder.bindData(repo, position)
        holder.itemBinding.itemClickListener = this
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun itemClicked(repository: Repository, position: Int) {
        mCustomLogs("Item Clicked, Repo Name: ${repository.name} and position: $position")
    }

    fun setData(data: List<Repository>) {
        mList = data
        notifyDataSetChanged()
    }
}