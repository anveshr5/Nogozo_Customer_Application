package com.anvesh.nogozocustomerapplication.ui.main.customer.search

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.ViewModelFactory
import com.anvesh.nogozocustomerapplication.ui.main.Communicator
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.ui.main.customer.shops.ShopListAdapter
import com.anvesh.nogozocustomerapplication.util.VerticalSpacingItemDecoration
import javax.inject.Inject

class GlobalSearchFragment(private val communicator: Communicator) :
    BaseFragment(R.layout.fragment_global_search), ShopListAdapter.OnShopClickInterface {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: GlobalSearchViewModel
    private lateinit var adapter: ShopListAdapter

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView


    companion object {
        var searchQuery: String? = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[GlobalSearchViewModel::class.java]

        searchView = view.findViewById(R.id.fragment_globalsearch_searchview)
        searchView.setIconifiedByDefault(false)
        searchView.requestFocus()

        recyclerView = view.findViewById(R.id.fragment_globalsearch_recyclerview)
        initRecycler()

        progressBar = view.findViewById(R.id.fragment_globalsearch_progressBar)
        errorText = view.findViewById(R.id.fragment_globalsearch_errorText)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.search(query.trimEnd())
                    searchQuery = query.trimEnd()
                }
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                errorText.visibility = View.GONE
                //        if (newText != null && newText.length>3) {
                //          searchQuery = newText
                //        viewModel.search(newText)
                //  }
                return true
            }
        })
        observeResult()
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(16))
        adapter = ShopListAdapter(this)
        adapter.removeAllItem()
        recyclerView.adapter = adapter
    }

    private fun observeResult() {
        viewModel.queryResult().removeObservers(viewLifecycleOwner)

        viewModel.queryResult().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                DataResource.Status.LOADING -> {
                    adapter.removeAllItem()
                    errorText.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
                DataResource.Status.SUCCESS -> {
                    adapter.setItemList(it.data)
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.GONE
                }
                DataResource.Status.ERROR -> {
                    adapter.removeAllItem()
                    errorText.visibility = View.VISIBLE
                    errorText.text = it.message
                    progressBar.visibility = View.GONE
                }
                DataResource.Status.NO_RESULT -> {
                    adapter.removeAllItem()
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    errorText.text = "Sorry, no shop have that"
                }
            }
        })
    }

    private fun hideKeyboard() {
        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var v: View? = activity!!.currentFocus
        if (v == null) {
            v = View(activity)
        }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onShopClick(shop: Shop) {
        if (shop.shopId == "-1")
            return
        communicator.onShopSelected(shop.shopId, shop.shopName, shop.shopAddress, shop.shopAreaId!!)
    }
}