package com.anvesh.nogozocustomerapplication.ui.main.customer.shops

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.util.Constants.CITY_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopListFragmentViewModel
//@Inject
//constructor(
//private val sessionManager: SessionManager,
//private val database: Database )
    : ViewModel() {

    private val shopList: MediatorLiveData<DataResource<ArrayList<Shop>>> = MediatorLiveData()

    private val database = Database()
    private val sessionManager = SessionManager()

    fun getShopsList(serviceId: String) {

        if (shopList.value != null) {
            if (shopList.value!!.status == DataResource.Status.LOADING) {
                return
            }
        }

        shopList.value = DataResource.loading()

        database.getShops(serviceId, sessionManager.currentSessionData[CITY_ID]!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    shopList.value = DataResource.error(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.Default).launch {
                        if (snapshot.value != null) {
                            val map = snapshot.value as HashMap<String, Any>
                            val shops: ArrayList<Shop> = ArrayList()
                            for ((key, value) in map) {
                                val shop = value as HashMap<String, String>
                                shops.add(
                                    Shop(
                                        shop["shopname"]!!,
                                        key,
                                        shop["imageurl"],
                                        shop["status"],
                                        shop["areaid"]
                                    )
                                )
                                Log.d("shopp", shop.toString())
                            }
                            shopList.postValue(DataResource.success(shops))
                        } else {
                            shopList.postValue(DataResource.error("No Shops in Your Area"))
                        }
                    }
                }
            })
    }

    fun getShopLiveData(): LiveData<DataResource<ArrayList<Shop>>> {
        return shopList
    }
}