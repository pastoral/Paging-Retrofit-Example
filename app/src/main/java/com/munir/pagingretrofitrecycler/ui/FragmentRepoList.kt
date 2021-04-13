package com.munir.pagingretrofitrecycler.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.munir.pagingretrofitrecycler.R
import com.munir.pagingretrofitrecycler.adapter.GithubRepoAdapter
import com.munir.pagingretrofitrecycler.adapter.OnRepoItemClickListner
import com.munir.pagingretrofitrecycler.adapters.RepoLoadStateAdapter
import com.munir.pagingretrofitrecycler.api.GitHubAPIs
import com.munir.pagingretrofitrecycler.container.ObjectContainer
import com.munir.pagingretrofitrecycler.data.model.Item
import com.munir.pagingretrofitrecycler.databinding.FragmentRepoListBinding
import com.munir.pagingretrofitrecycler.repository.GithubRepoPagingData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match

class FragmentRepoList : Fragment(), OnRepoItemClickListner {
    // TODO: Rename and change types of parameters

    private lateinit var viewModel: GithubRepoViewModel
    private lateinit var binding: FragmentRepoListBinding
    private var searchJob: Job? = null
    lateinit var githubRepoAdapter:GithubRepoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepoListBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = ObjectContainer.provideViewModelFactory()
        viewModel = ViewModelProvider(this,factory).get(GithubRepoViewModel::class.java)

        githubRepoAdapter = GithubRepoAdapter(this)
        binding.list.layoutManager = LinearLayoutManager(requireContext())
//        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//        binding.list.addItemDecoration(decoration)
        binding.list.setHasFixedSize(true)

        binding.list.adapter = githubRepoAdapter.withLoadStateHeaderAndFooter(
                header = RepoLoadStateAdapter{githubRepoAdapter.retry()},
                footer = RepoLoadStateAdapter{githubRepoAdapter.retry()}
        )

        githubRepoAdapter.addLoadStateListener {
            // show empty list
            val isListEmpty = it.refresh is LoadState.NotLoading && githubRepoAdapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.list.isVisible = it.source.refresh is LoadState.NotLoading

            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = it.source.refresh is LoadState.Loading

            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = it.source.refresh is LoadState.Error

            // Toast on any error
            val errorState = it.source.append as? LoadState.Error
                    ?: it.source.prepend as? LoadState.Error
                    ?: it.append as? LoadState.Error
                    ?: it.prepend as? LoadState.Error

            errorState?.let{
                Toast.makeText(
                        activity,
                        " Error occurred -  ${it.error}",
                        Toast.LENGTH_LONG
                ).show()

            }

        }

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        searchRepository(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.searchRepo.text.trim().toString())
    }


    private fun searchRepository(query:String){
        searchJob?.cancel()
        lifecycleScope.launch {
            viewModel.searchRepo(query).collectLatest {
                githubRepoAdapter.submitData(it)
            }
        }
    }


    private fun initSearch(query: String) {
        binding.searchRepo.setText(query)

        binding.searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            githubRepoAdapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    private fun updateRepoListFromInput() {
        binding.searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                searchRepository(it.toString())
            }
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Kotlin"
    }

    override fun onItemClick(item: Item, position: Int) {
              item.html_url.let{
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//              itemView.context.startActivity(intent)
                  startActivity(intent)
          }
    }

}