package com.munir.pagingretrofitrecycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.munir.pagingretrofitrecycler.databinding.FragmentRepoListBinding
import com.munir.pagingretrofitrecycler.databinding.ItemLoadStateFooterViewBinding

class RepoLoadStateAdapter(private val retry:()->Unit):LoadStateAdapter<RepoLoadStateViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RepoLoadStateViewHolder {
        val viewHolder = RepoLoadStateViewHolder(ItemLoadStateFooterViewBinding.inflate(
                LayoutInflater.from(parent.context),parent, false), retry)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RepoLoadStateViewHolder, loadState: LoadState) {
        holder.initialize(loadState)
    }
}


class RepoLoadStateViewHolder(private val binding: ItemLoadStateFooterViewBinding, retry:()->Unit) :
        RecyclerView.ViewHolder(binding.root){
            init{
                binding.retryButton.setOnClickListener{retry.invoke()}
            }
            fun initialize(loadState: LoadState){
                if(loadState is LoadState.Error){
                    binding.errorMsg.text = loadState.error.localizedMessage
                }
                binding.progressBar.isVisible = loadState is LoadState.Loading
                binding.retryButton.isVisible = loadState is LoadState.Error
                binding.errorMsg.isVisible = loadState is LoadState.Error
            }
        }