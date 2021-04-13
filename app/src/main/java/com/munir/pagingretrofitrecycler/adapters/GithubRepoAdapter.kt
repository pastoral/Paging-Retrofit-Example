package com.munir.pagingretrofitrecycler.adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.TextView

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.munir.pagingretrofitrecycler.R
import com.munir.pagingretrofitrecycler.data.model.Item
import com.munir.pagingretrofitrecycler.databinding.ItemRepo1Binding
import com.munir.pagingretrofitrecycler.databinding.ItemRepoBinding


class GithubRepoAdapter(var clickListner: OnRepoItemClickListner):PagingDataAdapter<Item,RepoViewHolder>(RepoComperator()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val repoViewHolder = RepoViewHolder(ItemRepo1Binding.inflate(LayoutInflater.from(parent.context),parent,false))
        return repoViewHolder
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.initialize(it,clickListner)
        }
    }
}


class RepoComperator:DiffUtil.ItemCallback<Item>(){
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
       return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.full_name == newItem.full_name
    }
}

class RepoViewHolder(private val itemBinding: ItemRepo1Binding) : RecyclerView.ViewHolder(itemBinding.root){

    private val name: TextView = itemBinding.repoName
    private val description: TextView = itemBinding.repoDescription
    //private val stars: TextView = itemBinding.repoStars
    private val language: TextView = itemBinding.repoLanguage
   // private val forks: TextView = itemBinding.repoForks

    private val owner : TextView = itemBinding.repoOwner

    private val item:Item?= null

//    init {
//        itemView.setOnClickListener {
//          item?.html_url?.let{
//              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//              itemView.context.startActivity(intent)
//          }
//        }
//    }

    @SuppressLint("SetTextI18n")
    fun initialize(item: Item? , action: OnRepoItemClickListner){
        if(item==null){
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
            description.visibility = View.GONE
            language.visibility = View.GONE
           // stars.text = resources.getString(R.string.unknown)
            //forks.text = resources.getString(R.string.unknown)

            owner.text = resources.getString(R.string.loading)
        }
        else{
            showRepoData(item)
        }

        itemView.setOnClickListener{
            action.onItemClick(item!!,adapterPosition)
        }

    }

    private fun showRepoData(item: Item) {

        name.text = item.full_name
        owner.text = item.name

        // if the description is missing, hide the TextView
        var descriptionVisibility = View.GONE
        if (item.description != null) {
            description.text = item.shortDescription
            descriptionVisibility = View.VISIBLE
        }
        description.visibility = descriptionVisibility

//        stars.text = item.stargazers_count.toString()
//        forks.text = item.forks_count.toString()

        // if the language is missing, hide the label and the value
        var languageVisibility = View.GONE
        if (!item.language.isNullOrEmpty()) {
            val resources = this.itemView.context.resources
            language.text = resources.getString(R.string.language, item.language)
            languageVisibility = View.VISIBLE
        }
        language.visibility = languageVisibility
    }

}


interface OnRepoItemClickListner{
    fun onItemClick(item: Item, position: Int)
}