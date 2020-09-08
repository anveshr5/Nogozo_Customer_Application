package com.anvesh.nogozocustomerapplication.ui.payment.customer.confirm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.VendorProfile
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_ID
import com.anvesh.nogozocustomerapplication.util.Constants.CITY_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CustomerConfirmFragmentViewModel
//@Inject
//constructor(
//    private val sessionManager: SessionManager,
//  private val database: Database)
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    private val deliveryStatus: MediatorLiveData<String> = MediatorLiveData()
    private val deliveryCharges: MediatorLiveData<String> = MediatorLiveData()
    private val shopPincode: MediatorLiveData<String> = MediatorLiveData()
    private val deliveryMinOrder: MediatorLiveData<String> = MediatorLiveData()
    private val vendor: MediatorLiveData<VendorProfile> = MediatorLiveData()


    val extraFare: MediatorLiveData<DataResource<HashMap<String, String>>> = MediatorLiveData()

    fun getUserAddress(): String {
        return sessionManager.getUserAddress()
    }

    fun getUserPhone(): String {
        return sessionManager.getUserPhone()
    }

    fun getUserName(): String {
        return sessionManager.getUserName()
    }

    fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getUserPincode(): String{
        return sessionManager.getUserPincode()
    }

    fun getVendorProfile(shopId: String){
        FirebaseDatabase.getInstance().getReference("/users/shop/$shopId/profile").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                vendor.value = snapshot.getValue(VendorProfile::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getVendorProfileLiveData(): MediatorLiveData<VendorProfile>{
        return vendor
    }

    fun getShopPincode(shopId: String): String{
        database.getShopAreaId(shopId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                shopPincode.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return "0"
    }

    fun getShopPincodeLiveData(): LiveData<String>{
        return shopPincode
    }

    fun getFareLiveData(): LiveData<DataResource<HashMap<String, String>>> {
        return extraFare
    }

    fun getFare(price: String, shopAreaId: String) {
        extraFare.value = DataResource.loading();

        database.getFare(
            price,
            sessionManager.currentSessionData[CITY_ID]!!,
            sessionManager.currentSessionData[AREA_ID]!!,
            shopAreaId
        )
            .continueWith {
                if (it.isSuccessful) {
                    val data = it.result!!.data as HashMap<String, String>
                    if (data.isNullOrEmpty())
                        extraFare.value = DataResource.error("Something went wrong")
                    else
                        extraFare.value = DataResource.success(data)
                } else {
                    extraFare.value = DataResource.error(it.exception!!.localizedMessage!!)
                }
            }
    }

    var NewOrderNo: String = "-1"
    var UpdateOrderNo: String = "-100"
    fun getOrderNo(shopId: String) {
        database.getOrderNumber(shopId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                NewOrderNo = snapshot.value as String
                Log.d("order2", snapshot.value as String)
                if (NewOrderNo != "-1")
                    updateOrderNo(shopId)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun updateOrderNo(shopId: String) {
        if (UpdateOrderNo == "-100") {
            UpdateOrderNo = (NewOrderNo.toInt() + 1).toString()
            database.getOrderNumber(shopId).setValue(UpdateOrderNo)
        }
    }

    fun buildOrderId(): Int {
        return NewOrderNo.toInt()
    }

    fun getCurrentDeliveryStatus(shopId: String){
        database.getDeliveryStatus(shopId).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryStatus.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getDeliveryStatusLiveData(): LiveData<String> {
        return deliveryStatus
    }

    fun getDeliveryCharges(shopId: String){
        database.getDeliveryCharges(shopId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryCharges.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getDeliveryChargesLiveData(): LiveData<String> {
        return deliveryCharges
    }

    fun getDeliveryMinOrder(shopId: String){
        database.getDeliveryMinOrder(shopId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryMinOrder.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getDeliveryMinOrderLiveData(): LiveData<String> {
        return deliveryMinOrder
    }
}