package com.anvesh.nogozocustomerapplication.ui.profile.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Area
import com.anvesh.nogozocustomerapplication.datamodels.AreaId
import com.anvesh.nogozocustomerapplication.datamodels.City
import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.ui.userdetails.CityResource
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerProfileFragmentViewModel
//@Inject
//constructor(
//      private val sessionManager: SessionManager,
//        private val database: Database)
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    private var cities: MediatorLiveData<CityResource<List<City>>> = MediatorLiveData()
    private var areas: MediatorLiveData<CityResource<List<Area>>> = MediatorLiveData()
    private var areaids: MediatorLiveData<CityResource<List<AreaId>>> = MediatorLiveData()
    private var userProfile: MediatorLiveData<DataResource<CustomerProfile>> = MediatorLiveData()

    fun getCities(): LiveData<CityResource<List<City>>> {
        cities.value = CityResource.loading()

        database.getCities().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                cities.value = CityResource.error(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.Default).launch {
                    val list: ArrayList<City> = ArrayList()
                    val map = snapshot.value as HashMap<String, String>
                    for ((key, value) in map) {
                        list.add(City(value, key))
                    }
                    cities.postValue(CityResource.success(list))
                }
            }
        })
        return cities
    }

    fun getAreaOfCity(cityId: String): LiveData<CityResource<List<Area>>> {
        areas.value = CityResource.loading()

        database.getAreas(cityId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                areas.value = CityResource.error(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                CoroutineScope(Dispatchers.Default).launch {
                    val list: ArrayList<Area> = ArrayList()
                    val map = snapshot.value as HashMap<String, String>
                    for ((key, value) in map) {
                        list.add(Area(value, key))
                    }
                    areas.postValue(CityResource.success(list))
                }
            }
        })

        return areas
    }

    fun getAreaIdsOfCity(cityId: String): LiveData<CityResource<List<AreaId>>>{
        areaids.value = CityResource.loading()

        Database().getAreaIds(cityId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                areaids.value = CityResource.error(error.message)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.Default).launch{
                    val list: ArrayList<AreaId> = ArrayList()
                    val map = snapshot.value as HashMap<String, String>
                    for((key, value) in map){
                        list.add(AreaId(value, key))
                    }
                    areaids.postValue(CityResource.success(list))
                }
            }
        })

        return areaids
    }

    fun getUserProfile() {
        userProfile.value = DataResource.loading()
        database.getUserProfile(userType_CUSTOMER)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val profile = snapshot.getValue<CustomerProfile>()
                        userProfile.value = DataResource.success(profile!!)
                    } else {
                        userProfile.value = DataResource.error("Something Went Wrong")
                    }
                }
            })
    }

    fun getProfileLiveData(): LiveData<DataResource<CustomerProfile>> {
        return userProfile
    }

    fun updateUserProfile(map: HashMap<String, Any>): Task<Void> {
        return database.setUserProfile(sessionManager.getUserType(), map)
    }

    fun saveProfileToLocal(map: HashMap<String, Any>) {
        sessionManager.saveCustomerProfileToLocal(map)
    }
}