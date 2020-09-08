package com.anvesh.nogozocustomerapplication.ui.main.customer.itemsInShop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.datamodels.VendorProfile
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.main.Communicator
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.ui.payment.ConfirmActivity
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ADDRESS
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_STATUS
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.util.VerticalSpacingItemDecoration
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsInShopFragment(
    private val communicator: Communicator
) : BaseFragment(R.layout.fragment_main_itemsinshop), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    lateinit var viewModel: ItemsInShopFragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var proceedButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

    private lateinit var itemsinshop_shopname_header: TextView
    private lateinit var vendor_address: TextView
    private lateinit var vendor_phone: TextView
    private lateinit var vendor_delivery_status: TextView
    private lateinit var vendor_delivery_chargers: TextView
    private lateinit var free_delivery_amount: TextView

    private var isDelivering = false
    lateinit var vendor: VendorProfile
    private lateinit var adapter: ItemsInShopAdapter
    private var shop: Shop? = null
    private lateinit var price: String
    var acceptOrders: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ItemsInShopFragmentViewModel::class.java]

        searchView = view.findViewById(R.id.fragment_main_itemsinshop_searchview)
        searchView.setIconifiedByDefault(false)
        recyclerView = view.findViewById(R.id.fragment_main_itemsinshop_recyclerview)
        progressBar = view.findViewById(R.id.fragment_iteminshops_progressBar)
        proceedButton = view.findViewById(R.id.fragment_main_iteminshop_proceed)
        proceedButton.setOnClickListener(this)

        itemsinshop_shopname_header = view.findViewById(R.id.itemsinsop_shopname_header)
        vendor_address = view.findViewById(R.id.itemsinsop_address_header)
        vendor_phone = view.findViewById(R.id.itemsinsop_phone_header)
        vendor_delivery_status = view.findViewById(R.id.itemsinsop_delivery_status_header)
        vendor_delivery_chargers = view.findViewById(R.id.itemsinsop_delivery_charges)
        free_delivery_amount = view.findViewById(R.id.itemsinsop_free_delivery_amount)


        initRecycler()

        shop = Shop(
            arguments!!.getString(SHOP_NAME, ""),
            arguments!!.getString(SHOP_ID, "-1"),
            "",
            arguments!!.getString(SHOP_STATUS, "-1"),
            arguments!!.getString(AREA_ID, "-1")
        )
        shop!!.shopAddress = arguments!!.getString(SHOP_ADDRESS, "")

        getShopStatus()
        setUpVendorDetails()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun setUpVendorDetails() {
        itemsinshop_shopname_header.text = shop!!.shopName
        viewModel.getLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            vendor = it.data

            vendor_address.text = vendor.address + " - ${vendor.areaid}"
            vendor_phone.text = vendor.phone
            if (vendor.deliverystatus == "Delivering") {
                isDelivering = true
                vendor_delivery_status.setTextColor(
                    resources.getColor(
                        R.color.green,
                        resources.newTheme()
                    )
                )
                vendor_delivery_chargers.visibility = View.VISIBLE
                vendor_delivery_status.text = vendor.deliverystatus
                vendor_delivery_chargers.text = "Delivery charges: ₹${vendor.deliverycharges}"
                free_delivery_amount.visibility = View.VISIBLE
                free_delivery_amount.text = "Order ₹${vendor.deliveryminorder} to get free delivery"
            } else {
                free_delivery_amount.visibility = View.GONE
                vendor_delivery_chargers.visibility = View.GONE
                vendor_delivery_status.setTextColor(
                    resources.getColor(
                        R.color.red,
                        resources.newTheme()
                    )
                )
                vendor_delivery_status.text = vendor.deliverystatus
            }
        })

        viewModel.getUserProfile(shop!!.shopId)
    }

    private fun getShopStatus() {
        viewModel.getShopLiveStatus().removeObservers(viewLifecycleOwner)

        viewModel.getShopLiveStatus().observe(viewLifecycleOwner, Observer {
            acceptOrders = it == "OPEN"
            getItems(shop!!.shopId)
        })
        viewModel.getShopStatus(shop!!.shopId)
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(8))
        adapter = ItemsInShopAdapter()
        recyclerView.adapter = adapter
    }

    private fun subscribeObserver() {
        adapter.getPriceLiveData().removeObservers(viewLifecycleOwner)
        adapter.getPriceLiveData().observe(this, Observer {
            price = it.toString()
            if (it == 0) {
                proceedButton.visibility = View.GONE
                if (isDelivering)
                    free_delivery_amount.text =
                        "Order ₹${vendor.deliveryminorder} to get free delivery"
                else
                    free_delivery_amount.visibility = View.GONE
            } else {
                if (isDelivering) {
                    if (vendor.deliveryminorder.toString().toInt() > price.toInt()) {
                        free_delivery_amount.visibility = View.VISIBLE
                        free_delivery_amount.text =
                            "Order ₹${
                                vendor.deliveryminorder.toString().toInt() - price.toInt()
                            } more to get free delivery"
                        vendor_delivery_chargers.visibility = View.VISIBLE
                    } else {
                        free_delivery_amount.visibility = View.VISIBLE
                        free_delivery_amount.text =
                            "Free delivery on this order!"
                        vendor_delivery_chargers.visibility = View.GONE
                    }
                } else if (!isDelivering) {
                    free_delivery_amount.visibility = View.GONE
                }
                proceedButton.visibility = View.VISIBLE
                proceedButton.text = "Total Price: ₹$it\nGoto Cart >"
            }
        })
    }

    private fun getItems(shopId: String) {
        viewModel.getItems().removeObservers(viewLifecycleOwner)

        viewModel.getItems(shopId).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                DataResource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    adapter.setData(it.data, acceptOrders)
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
        when (v!!.id) {
            R.id.fragment_main_iteminshop_proceed -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val map = adapter.getSelectedItem()
                    withContext(Main) {
                        val i = Intent(context, ConfirmActivity::class.java)
                        i.putExtra(USER_TYPE, userType_CUSTOMER)
                        val orderData: HashMap<String, Any> = HashMap()
                        orderData["shopid"] = shop!!.shopId
                        orderData["shopname"] = shop!!.shopName
                        orderData["shopareaid"] = vendor.areaid.toString()
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
