package com.anvesh.nogozocustomerapplication.ui.main.customer.itemsInShop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.anvesh.nogozocustomerapplication.ui.payment.ConfirmActivity
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ADDRESS
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_STATUS
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.util.VerticalSpacingItemDecoration
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.ViewModelFactory
import com.anvesh.nogozocustomerapplication.ui.main.Communicator
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ItemsInShopFragment(
    private val communicator: Communicator
): BaseFragment(R.layout.fragment_main_itemsinshop), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    lateinit var viewModel: ItemsInShopFragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var proceedButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var itemsinshop_header: TextView
    private lateinit var searchView: SearchView

    private lateinit var adapter: ItemsInShopAdapter
    private var shop: Shop? = null
    private lateinit var price: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ItemsInShopFragmentViewModel::class.java]

        searchView = view.findViewById(R.id.fragment_main_itemsinshop_searchview)
        searchView.setIconifiedByDefault(false)
        recyclerView = view.findViewById(R.id.fragment_main_itemsinshop_recyclerview)
        progressBar = view.findViewById(R.id.fragment_iteminshops_progressBar)
        proceedButton = view.findViewById(R.id.fragment_main_iteminshop_proceed)
        proceedButton.setOnClickListener(this)
        itemsinshop_header = view.findViewById(R.id.itemsinsop_header)

        initRecycler()

        shop = Shop(
            arguments!!.getString(SHOP_NAME, ""),
            arguments!!.getString(SHOP_ID, "-1"),
            "",
            arguments!!.getString(SHOP_STATUS, "-1"),
            arguments!!.getString(AREA_ID, "-1")
        )
        shop!!.shopAddress = arguments!!.getString(SHOP_ADDRESS, "")

        getItems(shop!!.shopId)
        itemsinshop_header.text = shop!!.shopName

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return true
            }
        })
        subscribeObserver()
    }

    private fun initRecycler(){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(8))
        adapter = ItemsInShopAdapter()
        recyclerView.adapter = adapter
    }

    private fun subscribeObserver(){
        adapter.getPriceLiveData().removeObservers(viewLifecycleOwner)
        adapter.getPriceLiveData().observe(this, Observer {
            if(it == 0){
                proceedButton.visibility = View.INVISIBLE
            }else{
                proceedButton.visibility = View.VISIBLE
                proceedButton.text = "Total Price: ₹$it\nGoto Cart >"
            }
            price = it.toString()
        })
    }

    private fun getItems(shopId: String){
        viewModel.getItems().removeObservers(viewLifecycleOwner)

        viewModel.getItems(shopId).observe(viewLifecycleOwner, Observer{
            when(it.status){
                DataResource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    adapter.setData(it.data)
                    Log.d("items",it.data.toString())
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fragment_main_iteminshop_proceed -> {
                CoroutineScope(Dispatchers.Default).launch{
                    val map = adapter.getSelectedItem()
                    withContext(Main){
                        val i = Intent(context, ConfirmActivity::class.java)
                        i.putExtra(USER_TYPE, userType_CUSTOMER)
                        val orderData: HashMap<String, Any> = HashMap()
                        orderData["shopid"] = shop!!.shopId
                        orderData["shopname"] = shop!!.shopName
                        orderData["shopareaid"] = shop!!.shopAreaId!!
                        orderData["shopaddress"] = shop!!.shopAddress!!
                        orderData["price"] = price
                        orderData["itemprice"] = price
                        orderData["status"] = "0"
                        orderData["items"] = map
                        i.putExtra("order", orderData)
                        startActivity(i)
                    }
                }
            }
        }
    }
}
