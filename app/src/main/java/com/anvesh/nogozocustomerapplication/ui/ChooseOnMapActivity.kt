package com.anvesh.nogozocustomerapplication.ui

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anvesh.nogozocustomerapplication.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.anvesh.nogozocustomerapplication.util.Constants.CHOOSE_ON_MAP_REQUEST_CODE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class ChooseOnMapActivity:
    AppCompatActivity(),
    View.OnClickListener,
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener {

    //private lateinit var gMap: GoogleMap
    private lateinit var fab: FloatingActionButton
    private lateinit var addressField: TextView

    private var target: LatLng? = null
    private var address: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_on_map)

        //val supportMapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        //supportMapFragment.getMapAsync(this)

        fab = findViewById(R.id.fab)
        addressField = findViewById(R.id.address_field)
        fab.setOnClickListener(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        //gMap = p0!!
        //gMap.setOnCameraIdleListener(this)
    }

    override fun onCameraIdle() {
//        target = gMap.cameraPosition.target!!
//        loading(true)
//        CoroutineScope(IO).launch {
//            val a = getAddressFromCoordinate(target)
//            withContext(Main){
//                address = a
//                addressField.text = address
//                loading(false)
//            }
//        }
    }

//    private fun getAddressFromCoordinate(latLng: LatLng?): String{
//
//        val gcd = Geocoder(this, Locale.getDefault())
//        val addresses: List<Address>
//        val address: Address
//        val strAddress: StringBuilder = StringBuilder()
//        try {
//            println("Calling GEOCODER")
//            addresses = gcd.getFromLocation(latLng!!.latitude, latLng.longitude, 1)
//            if (addresses.isNotEmpty()){
//                address = addresses[0]
//                println(address.toString())
//                address.getAddressLine(0)
//                strAddress.append(address.getAddressLine(0))
//                println(address.getAddressLine(0))
////                for (i in 0 until address.maxAddressLineIndex){
////                    strAddress.append(address.getAddressLine(i))
////                    println(address.getAddressLine(i))
////                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return strAddress.toString()
//    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fab -> {
//                val i = Intent()
//                target?.let { target ->
//                    i.putExtra("lat", target.latitude)
//                    i.putExtra("lon", target.longitude)
//                    address?.let {address ->
//                        if(address.isNotEmpty()){
//                            i.putExtra("address", address)
//                            setResult(Activity.RESULT_OK,i)
//                            finish()
//                        }
//                    }
//                }
            }
        }
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            println("Change Loading to True")
        }
    }

}