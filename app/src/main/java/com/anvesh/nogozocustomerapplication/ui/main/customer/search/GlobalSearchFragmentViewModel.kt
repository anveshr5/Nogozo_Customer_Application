package com.anvesh.nogozocustomerapplication.ui.main.customer.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlobalSearchViewModel
//@Inject constructor(
  //  private val database: Database,
    //private val sessionManager: SessionManager)
 : ViewModel() {

    private val database: Database = Database()
    private val sessionManager: SessionManager = SessionManager()

    private val queryResult: MediatorLiveData<DataResource<ArrayList<Shop>>> = MediatorLiveData()
    private var currentSearch: String = ""

    fun search(query: String) {
        if (queryResult.value != null) {
            if (currentSearch == query)
                return
        }
        queryResult.value = DataResource.loading()

        database.searchItemsinCity(query, sessionManager.getCityId())
            .continueWith {
                if (it.isSuccessful) {
                    val result = it.result
                    if (result != null) {
                        val data = result.data as HashMap<String, String>
                        Log.d("result", "$data")
                        if (data.isEmpty()) {
                            queryResult.value = DataResource.emptyResult()
                        } else {
                            if (data.containsKey("error")) {
                                queryResult.value = DataResource.error(data["error"]!!)
                            } else {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val shopList: ArrayList<Shop> = ArrayList()
                                    for ((key, value) in data) {
                                        shopList.add(Shop(value, key, null, null, "-1","-1"))
                                    }
                                    queryResult.postValue(DataResource.success(shopList))
                                }
                            }
                        }
                    }
                } else {
                    queryResult.value = DataResource.error(it.exception!!.message!!)
                }
            }
    }

    fun queryResult(): LiveData<DataResource<ArrayList<Shop>>> {
        return queryResult
    }
}