package com.anvesh.nogozocustomerapplication.ui.main.customer.shops

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anvesh.nogozocustomerapplication.util.Constants.SERVICE_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SERVICE_NAME
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Services
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.ViewModelFactory
import com.anvesh.nogozocustomerapplication.ui.main.Communicator
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.util.VerticalSpacingItemDecoration
import javax.inject.Inject

class ShopListFragment(
    private val communicator: Communicator
): BaseFragment(R.layout.fragment_main_shops), ShopListAdapter.OnShopClickInterface{

    //@Inject
    //lateinit var factory: ViewModelFactory
    //@Inject
    lateinit var sessionManager: SessionManager

    private lateinit var viewModel: ShopListFragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    private lateinit var adapter: ShopListAdapter

    private var service: Services? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ShopListFragmentViewModel::class.java]

        recyclerView = view.findViewById(R.id.fragment_main_shop_recyclerview)
        progressBar = view.findViewById(R.id.fragment_shops_progressBar)
        swipeRefresh = view.findViewById(R.id.fragment_shops_swipeRefresh)
        searchView = view.findViewById(R.id.fragment_main_shop_searchview)
        searchView.setIconifiedByDefault(false)

        initRecycler()

        service = Services(arguments!!.getString(SERVICE_ID,"-1"), arguments!!.getString(SERVICE_NAME,"-1"))

        swipeRefresh.setOnRefreshListener{
            swipeRefresh.isRefreshing = false
            viewModel.getShopsList(service?.serviceId!!)
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return true
            }
        })

        getShops(service?.serviceId!!)

    }

    private fun initRecycler(){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(16))
        adapter = ShopListAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun getShops(serviceId: String){
        viewModel.getShopLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getShopLiveData().observe(viewLifecycleOwner, Observer {
            when(it!!.status){
                DataResource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    adapter.setItemList(it.data)
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
        viewModel.getShopsList(serviceId)
    }

    override fun onShopClick(shop: Shop) {
        if(shop.shopId == "-1")
            return
        communicator.onShopSelected(shop.shopId, shop.shopName, shop.shopAddress, shop.shopAreaId!!)
    }
}