package com.anvesh.nogozocustomerapplication.ui.orders.customer.past

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Order
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.util.Constants

class CustomerPastOrdersFragmentViewModel
//@Inject
//constructor(
//    private val sessionManager: SessionManager)
: ViewModel() {

    private val sessionManager: SessionManager =SessionManager()

    private var pastOrders: MediatorLiveData<DataResource<ArrayList<Order>>> = MediatorLiveData()
    private val tempArrayList: ArrayList<Order> = ArrayList()

    fun getLiveData(): LiveData<DataResource<ArrayList<Order>>> {
        return pastOrders
    }

    fun getPastOrderOrders(){
        if(pastOrders.value != null){
            if(pastOrders.value!!.status == DataResource.Status.LOADING)
                return
        }

        pastOrders.value = DataResource.loading()
        Database().getPastOrder(Constants.userType_CUSTOMER, sessionManager.getUserId())
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    pastOrders.value = DataResource.error(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null){
                        val orderIds = snapshot.value as HashMap<String, String>
                        if(orderIds.size == 0){
                            pastOrders.value = DataResource.error("No Orders")
                        }else{
                            getOrderDetails(orderIds.keys)
                        }
                    }else{
                        pastOrders.value = DataResource.error("No Orders")
                    }
                }
            })
    }

    private fun getOrderDetails(orderId: Set<String>){
        for(key in orderId){
            Database().getOrderDetails(key).addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val order = snapshot.getValue<Order>()!!
                    tempArrayList.add(order)

                    if(tempArrayList.size == orderId.size){
                        pastOrders.value = DataResource.success(tempArrayList.clone() as ArrayList<Order>)
                        tempArrayList.clear()
                    }
                }

            })
        }
    }
}