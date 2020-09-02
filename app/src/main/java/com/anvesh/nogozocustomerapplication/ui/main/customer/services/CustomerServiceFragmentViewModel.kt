package com.anvesh.nogozocustomerapplication.ui.main.customer.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Services
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerServiceFragmentViewModel
//@Inject
//constructor(
//    val sessionManager: SessionManager,
//    val database: Database)
: ViewModel(){

    private val services: MediatorLiveData<DataResource<List<Services>>> = MediatorLiveData()
//    private var areas: MediatorLiveData<CityResource<ArrayList<Area>>> = MediatorLiveData()

    val sessionManager: SessionManager = SessionManager()
    val database: Database = Database()

    fun getLiveData(): LiveData<DataResource<List<Services>>>{
        return services
    }

    fun getServices(){

        if(services.value != null){
            if(services.value!!.status == DataResource.Status.LOADING)
                return
        }

        services.value = DataResource.loading()

        database.getServices().addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                services.value = DataResource.error(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Default).launch{
                    val list: ArrayList<Services> = ArrayList()
                    for(snap in snapshot.children){
                        val a = snap.getValue<Services>()
                        if(a != null){
                            a.serviceId = snap.key
                            list.add(a)
                        }
                    }
                    services.postValue(DataResource.success(list))
                }
            }
        })
    }
    /*
    * used when there was change-area button on home screen
    * */
//    fun getAreaLiveData(): LiveData<CityResource<ArrayList<Area>>>{
//        return areas
//    }
//
//    fun getAreaOfCity(cityId: String){
//        areas.value = CityResource.loading()
//
//        database.getAreas(cityId).addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                areas.value = CityResource.error(error.message)
//            }
//            override fun onDataChange(snapshot: DataSnapshot) {
//                CoroutineScope(Dispatchers.Default).launch{
//                    val list: ArrayList<Area> = ArrayList()
//                    val map = snapshot.value as HashMap<String, String>
//                    for((key, value) in map){
//                        list.add(Area(value, key))
//                    }
//                    areas.postValue(CityResource.success(list))
//                }
//            }
//        })
//    }
}