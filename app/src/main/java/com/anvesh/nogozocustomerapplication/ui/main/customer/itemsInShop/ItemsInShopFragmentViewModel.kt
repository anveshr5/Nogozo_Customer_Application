package com.anvesh.nogozocustomerapplication.ui.main.customer.itemsInShop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.datamodels.Item
import com.anvesh.nogozocustomerapplication.datamodels.VendorProfile
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.util.Constants.userType_VENDOR
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsInShopFragmentViewModel
//@Inject
//constructor(
//    private val database: Database)
    : ViewModel() {

    val database = Database()

    var items: MutableLiveData<DataResource<ArrayList<Item>>> = MutableLiveData()
    var shopStatus: MediatorLiveData<String> = MediatorLiveData()

    private var userProfile: MediatorLiveData<DataResource<VendorProfile>> = MediatorLiveData()


    fun getUserProfile(shopId: String) {
        FirebaseDatabase.getInstance().getReference("/users/shop/$shopId/profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val profile = snapshot.getValue<VendorProfile>()
                        userProfile.value = DataResource.success(profile!!)
                    }
                }
            })
    }

    fun getLiveData(): LiveData<DataResource<VendorProfile>> {
        return userProfile
    }

    fun getItems(shopId: String): LiveData<DataResource<ArrayList<Item>>> {
        items.value = DataResource.loading()

        database.getItems(shopId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                items.value = DataResource.error(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        val data: ArrayList<Item> = ArrayList()
                        for (item in snapshot.children) {
                            val i = Item(item.key)
                            val v = item.value as HashMap<String, String>
                            i.itemName = v["itemname"]
                            i.itemPrice = v["itemprice"]!!
                            i.itemMRP = v["itemMRP"]
                            i.itemQuantity = v["quantity"]
                            i.itemGroup = v["itemGroup"]
                            i.isAvailable = v["isAvailable"] == "true"
                            i.itemImageUrl = v["itemimage"]
                            data.add(i)
                        }
                        items.postValue(DataResource.success(data))

                    }
                } else {
                    items.postValue(DataResource.error("No items in this Shop"))
                }
            }
        })
        return items
    }

    fun getItems(): LiveData<DataResource<ArrayList<Item>>> {
        return items
    }

    fun getShopStatus(shopId: String) {
        database.getShopStatus(shopId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                shopStatus.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getShopLiveStatus(): MediatorLiveData<String> {
        return shopStatus
    }

}