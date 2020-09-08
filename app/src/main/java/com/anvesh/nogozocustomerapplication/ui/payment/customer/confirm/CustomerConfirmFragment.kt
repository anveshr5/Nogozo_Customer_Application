package com.anvesh.nogozocustomerapplication.ui.payment.customer.confirm

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.VendorProfile
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.ui.main.MainActivity
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CustomerConfirmFragment : BaseFragment(R.layout.fragment_payment_confirm),
    View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: CustomerConfirmFragmentViewModel

    private lateinit var confirmButton: MaterialButton
    private lateinit var itemsText: TextView
    private lateinit var deliveryChargesView: TextView
    private lateinit var grandTotal: TextView
    private lateinit var itemPrice: TextView
    private lateinit var instruction: TextView
    private lateinit var priceWrapper: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var deliveryStatus: TextView
    private lateinit var selectDeliveryTag: TextView
    private lateinit var addAmountForFreeDeliveryWrapper: LinearLayout
    private lateinit var addAmountForFreeDelivery: TextView
    //private lateinit var deliveryChargesLayout: LinearLayout

    private lateinit var deliveryModeSpinner: Spinner
    var modeOfDeliverySelect: Boolean = false
    var isDeliverable: Boolean = false
    var freeDelivery: Boolean = false
    var deliveryCharges = 0
    var minAmount = 10000

    lateinit var vendor: VendorProfile
    private lateinit var orderData: HashMap<String, Any>
    private lateinit var fare: HashMap<String, String>
    private var totalFare: Int = 0
    var pincode = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        confirmButton = view.findViewById(R.id.customer_confirm_confirm_button)
        confirmButton.setOnClickListener(this)
        deliveryStatus = view.findViewById(R.id.deliveryStatus)
        itemsText = view.findViewById(R.id.customer_confirm_items)
        deliveryChargesView = view.findViewById(R.id.delivery_charges)
        addAmountForFreeDelivery = view.findViewById(R.id.customer_free_delivery_amount)
        addAmountForFreeDeliveryWrapper = view.findViewById(R.id.addAmountForFreeDeliveryTag)
        grandTotal = view.findViewById(R.id.customer_confirm_total_price)
        deliveryModeSpinner = view.findViewById(R.id.deliveryModeSpinner)
        itemPrice = view.findViewById(R.id.customer_confirm_base_price)
        instruction = view.findViewById(R.id.customer_confirm_instruction)
        progressBar = view.findViewById(R.id.customer_confirm_progressbar)
        selectDeliveryTag = view.findViewById(R.id.select_delivery_tag)
        priceWrapper = view.findViewById(R.id.customer_confirm_price_wrapper)
        priceWrapper.setOnClickListener(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[CustomerConfirmFragmentViewModel::class.java]

        orderData = arguments!!.getSerializable("order") as HashMap<String, Any>
        viewModel.getOrderNo(orderData["shopid"].toString())
        viewModel.getFare(orderData["itemprice"] as String, orderData["shopareaid"] as String)

        getVendorProfile()
        setOrderItemsToText()
    }

    private fun getVendorProfile() {
        viewModel.getVendorProfileLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getVendorProfileLiveData().observe(viewLifecycleOwner, Observer {
            vendor = it
            getPincodes()
        })

        viewModel.getVendorProfile(orderData["shopid"].toString())
    }

    private fun getPincodes() {
        val userPincode = SessionManager().getAreaId()
        pincode = vendor.areaid.toString()
        if (vendor.areaid == userPincode) {
            isDeliverable = true
            getCurrentDeliveryStatus()
            Log.d("pincode", userPincode + " 6  $pincode")
        } else {
            isDeliverable = false
            deliveryStatus.text = "Location too far"
            Log.d("pincode", userPincode + "   $pincode")
            deliveryStatus.setTextColor(resources.getColor(R.color.red, resources.newTheme()))
            setUpNoDeliverySpinner()
        }
    }

    private fun getMinAmountDelivery() {
        minAmount = vendor.deliveryminorder!!.toInt()
        if (minAmount <= orderData["itemprice"].toString().toInt()) {
            freeDelivery = true
            setUpDeliverySpinner(freeDelivery)
        } else {
            freeDelivery = false
            setUpDeliverySpinner(freeDelivery)
            getDeliveryCharges()
            Toast.makeText(
                activity as Context,
                "Add ₹${
                    minAmount - orderData["itemprice"].toString().toInt()
                } to avail free delivery",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCurrentDeliveryStatus() {
        viewModel.getDeliveryStatusLiveData().removeObservers(viewLifecycleOwner)
        viewModel.getDeliveryStatusLiveData().observe(viewLifecycleOwner, Observer {
            if (it == "Delivering" && isDeliverable) {
                deliveryStatus.text = it
                deliveryStatus.setTextColor(resources.getColor(R.color.green, resources.newTheme()))
                getMinAmountDelivery()
            } else if (it == "Not Delivering" && isDeliverable) {
                deliveryStatus.text = it
                deliveryStatus.setTextColor(resources.getColor(R.color.red, resources.newTheme()))
                setUpNoDeliverySpinner()
            } else if (!isDeliverable) {
                deliveryStatus.text = "Location too far"
                deliveryStatus.setTextColor(resources.getColor(R.color.red, resources.newTheme()))
                setUpNoDeliverySpinner()
            }
        })
        viewModel.getCurrentDeliveryStatus(orderData["shopid"].toString())
    }

    private fun setUpDeliverySpinner(freeDelivery: Boolean) {
        progressBar.visibility = View.GONE
        val itemList = arrayOf(
            "Select mode of delivery",
            "Pickup and pay at shop",
            //"Prepaid and pickup at Shop",
            //"Prepaid and Home Delivery",
            "Pay on Home Delivery"
        )

        deliveryModeSpinner.adapter = ArrayAdapter(
            context!!.applicationContext,
            android.R.layout.simple_list_item_1,
            itemList
        )

        deliveryModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        modeOfDeliverySelect = false
                    }
                    1 -> {
                        modeOfDeliverySelect = true
                        //  orderData["paid"] = "No"
                        orderData["delivery"] = "No"
                        updateBill("nodelivery")
                    }
                    //2->{
                    //    modeOfDeliverySelect = true
                    //    orderData["paid"] = "Yes"
                    //    orderData["delivery"] = "No"
                    //    updateBill("nodelivery")
                    //}
                    //3->{
                    //    modeOfDeliverySelect = true
                    //    orderData["delivery"] = "Yes"
                    //    orderData["deliverycharges"] = deliveryCharges.toString()
                    //    orderData["paid"] = "Yes"
                    //    updateDeliveryBill(freeDelivery)
                    //}
                    2 -> {
                        modeOfDeliverySelect = true
                        orderData["delivery"] = "Yes"
                        orderData["deliverycharges"] = deliveryCharges.toString()
                        //  orderData["paid"] = "No"
                        updateDeliveryBill(freeDelivery)
                    }
                }
                if (position != 0) {
                    selectDeliveryTag.visibility = View.GONE
                    priceWrapper.visibility = View.VISIBLE
                    confirmButton.visibility = View.VISIBLE
                } else {
                    selectDeliveryTag.visibility = View.VISIBLE
                    priceWrapper.visibility = View.GONE
                    confirmButton.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun updateDeliveryBill(freeDelivery: Boolean) {
        if (freeDelivery && orderData["delivery"].toString() == "Yes") {
            orderData["deliverycharges"] = "0"
            updateBill("freedelivery")
        } else if (orderData["delivery"].toString() == "Yes") {
            getDeliveryCharges()
            updateBill("paiddelivery")
        } else if (orderData["delivery"].toString() == "No") {
            updateBill("nodelivery")
        }
    }

    private fun getDeliveryCharges() {
        deliveryCharges = vendor.deliverycharges!!.toInt()
        deliveryChargesView.text = "₹${vendor.deliverycharges!!}"
    }

    private fun setUpNoDeliverySpinner() {
        progressBar.visibility = View.GONE
        val itemList = arrayOf(
            //"Select mode of payment",
            "Pay and pickup at shop",
            //"Prepaid and pickup at Shop"
            "Pay On Home Delivery(Not Available)"
        )
        deliveryModeSpinner.adapter = ArrayAdapter(
            context!!.applicationContext,
            android.R.layout.simple_list_item_1,
            itemList
        )

        deliveryModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    //0 -> {
                    //    modeOfDeliverySelect = false
                    //}
                    0 -> {
                        modeOfDeliverySelect = true
                        //  orderData["paid"] = "No"
                        updateBill("nodelivery")
                    }
                    //2 -> {
                    //    modeOfDeliverySelect = true
                    //    orderData["paid"]="Yes"
                    //    updateBill("nodelivery")
                    //}
                    1 -> {
                        Toast.makeText(
                            activity as Context,
                            "Delivery is currently not available",
                            Toast.LENGTH_SHORT
                        ).show()
                        deliveryModeSpinner.setSelection(0)
                    }
                }
                //if(position!=0) {
                selectDeliveryTag.visibility = View.GONE
                priceWrapper.visibility = View.VISIBLE
                confirmButton.visibility = View.VISIBLE
                //}
                //} else {
                //selectDeliveryTag.visibility = View.VISIBLE
                //priceWrapper.visibility = View.GONE
                //confirmButton.visibility = View.GONE
                //}
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun updateBill(mode: String) {
        when (mode) {
            "nodelivery" -> {
                deliveryCharges = 0
                deliveryChargesView.text = "No delivery"
                addAmountForFreeDeliveryWrapper.visibility = View.GONE
                orderData["deliverycharges"] = "0"
                orderData.remove("customeraddress")
                orderData.remove("customerphone")
                subscribeObserver()
            }
            "freedelivery" -> {
                deliveryCharges = 0
                deliveryChargesView.text = "Free Delivery"
                addAmountForFreeDeliveryWrapper.visibility = View.GONE
                orderData["customeraddress"] = viewModel.getUserAddress()
                orderData["customerphone"] = viewModel.getUserPhone()
                subscribeObserver()
            }
            "paiddelivery" -> {
                deliveryChargesView.text = "₹$deliveryCharges"
                addAmountForFreeDeliveryWrapper.visibility = View.VISIBLE
                addAmountForFreeDelivery.text =
                    "₹${minAmount - orderData["itemprice"].toString().toInt()}"
                orderData["customeraddress"] = viewModel.getUserAddress()
                orderData["customerphone"] = viewModel.getUserPhone()
                orderData["deliverycharges"] = deliveryCharges.toString()
                subscribeObserver()
            }
        }
    }

    private fun subscribeObserver() {
        viewModel.getFareLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getFareLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                DataResource.Status.SUCCESS -> {
                    fare = it.data
                    totalFare = orderData["itemprice"].toString()
                        .toInt() + deliveryCharges
                    orderData["price"] = totalFare.toString()
                    setItemsToText()
                    progressBar.visibility = View.GONE
                    confirmButton.visibility = View.VISIBLE
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    confirmButton.visibility = View.GONE
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    confirmButton.visibility = View.GONE
                }
            }
        })
    }

    private fun setItemsToText() {
        itemPrice.text = "₹ ${orderData["itemprice"] as String}"
        grandTotal.text = "₹ $totalFare"
    }

    private fun setOrderItemsToText() {
        CoroutineScope(Default).launch {
            var itemString = ""
            for ((key, value) in orderData["items"] as HashMap<String, Any>) {
                val details = value as HashMap<String, String>
                itemString += "${details["times"]}x ${details["itemname"]}\n"
                //key=id, value={name, quantity, times}
            }
            itemString.removeSuffix("\n")
            withContext(Main) {
                itemsText.text = itemString
            }
        }
    }

    private fun createOrderId() {
        val shopId = orderData["shopid"].toString()
        val orderno: Int = viewModel.buildOrderId()
        val shopname = orderData["shopname"].toString()
        val shopPincode = pincode.takeLast(2)

        orderData["orderId"] = "${shopname.substring(0, 3)}/${shopPincode}/${orderno}"
    }

    private fun createDialog() {
        val b = AlertDialog.Builder(context!!)
        b.setTitle("Order Booked")
        b.setMessage("Your Order have been Booked.")
        b.setPositiveButton("YAY!!") { _: DialogInterface, _: Int ->
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(USER_TYPE, userType_CUSTOMER)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        val dialog = b.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun loadingDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.show()
    }

    private fun chargesDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_extra_charge)

        val close: MaterialButton = dialog.findViewById(R.id.dialog_extra_charge_close)
        close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.customer_confirm_price_wrapper -> {
                chargesDialog()
            }
            R.id.customer_confirm_confirm_button -> {
                if (modeOfDeliverySelect) {
                    loadingDialog()
                    val instructions = instruction.text.toString()
                    CoroutineScope(IO).launch {
                        orderData["customername"] = viewModel.getUserName()
                        orderData["customerid"] = viewModel.getUserId()
                        if (instructions.isNotEmpty())
                            orderData["shopinstruction"] = instructions
                        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
                        val timeFormatter = SimpleDateFormat("HH:mm")
                        orderData["date"] = dateFormatter.format(Date())
                        orderData["time"] = timeFormatter.format(Date())
                        val datetimeFormatter = SimpleDateFormat("yyyyMMddHHmmss")
                        orderData["datetime"] = datetimeFormatter.format(Date())
                        createOrderId()
                        Database().createOrder().setValue(orderData).addOnCompleteListener {
                            if (it.isSuccessful) {
                                createDialog()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        activity as Context,
                        "Please select a delivery mode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
