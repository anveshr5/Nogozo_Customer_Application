package com.anvesh.nogozocustomerapplication.ui.userdetails.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Area
import com.anvesh.nogozocustomerapplication.datamodels.City
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.userdetails.CityResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerDetailsFragmentViewModel
//@Inject
//constructor(
//    private val sessionManager: SessionManager)
: ViewModel() {

    private val sessionManager: SessionManager = SessionManager()

    private var cities: MediatorLiveData<CityResource<List<City>>> = MediatorLiveData()
    private var areas: MediatorLiveData<CityResource<List<Area>>> = MediatorLiveData()

    fun getCities(): LiveData<CityResource<List<City>>>{
        cities.value = CityResource.loading()

        Database().getCities().addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                cities.value = CityResource.error(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.Default).launch{
                    val list: ArrayList<City> = ArrayList()
                    val map = snapshot.value as HashMap<String, String>
                    for((key, value) in map){
                        list.add(City(value, key))
                    }
                    cities.postValue(CityResource.success(list))
                }
            }
        })
        return cities
    }

    fun getAreaOfCity(cityId: String): LiveData<CityResource<List<Area>>>{
        areas.value = CityResource.loading()

        Database().getAreas(cityId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                areas.value = CityResource.error(error.message)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.Default).launch{
                    val list: ArrayList<Area> = ArrayList()
                    val map = snapshot.value as HashMap<String, String>
                    for((key, value) in map){
                        list.add(Area(value, key))
                    }
                    areas.postValue(CityResource.success(list))
                }
            }
        })

        return areas
    }

    fun updateUserProfile(map: HashMap<String, Any>): Task<Void> {
        return Database().setUserProfile(sessionManager.getUserType() ,map)
    }

    fun saveProfileToLocal(map: HashMap<String, Any>){
        sessionManager.saveCustomerProfileToLocal(map)
    }
}