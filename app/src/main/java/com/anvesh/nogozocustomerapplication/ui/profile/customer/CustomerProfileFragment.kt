package com.anvesh.nogozocustomerapplication.ui.profile.customer

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.anvesh.nogozocustomerapplication.util.Constants
import com.anvesh.nogozocustomerapplication.util.Constants.DIALOG_TYPE_AREA
import com.anvesh.nogozocustomerapplication.util.Constants.DIALOG_TYPE_CITY
import com.anvesh.nogozocustomerapplication.util.Helper
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Area
import com.anvesh.nogozocustomerapplication.datamodels.City
import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.ui.BaseFragment
import com.anvesh.nogozocustomerapplication.ui.ViewModelFactory
import com.anvesh.nogozocustomerapplication.ui.main.DataResource
import com.anvesh.nogozocustomerapplication.ui.userdetails.AreaListAdapter
import com.anvesh.nogozocustomerapplication.ui.userdetails.CityListAdapter
import com.anvesh.nogozocustomerapplication.ui.userdetails.CityResource
import javax.inject.Inject

class CustomerProfileFragment: BaseFragment(R.layout.fragment_profile_customer), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    lateinit var viewModel: CustomerProfileFragmentViewModel

    private lateinit var citySpinner: TextView
    private lateinit var areaSpinner: TextView
    private lateinit var cityCard: CardView
    private lateinit var areaCard: CardView
    private lateinit var addressField: TextInputEditText
    private lateinit var nameField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var selectedCity: City? = null
    private var selectedArea: Area? = null
    private var selectedAddress: String? = null

    private var oldProfile: CustomerProfile? = null
    private var newProfile: CustomerProfile? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[CustomerProfileFragmentViewModel::class.java]

        progressBar = view.findViewById(R.id.customer_profile_progressbar)
        areaCard = view.findViewById(R.id.customer_profile_area_wrapper)
        cityCard = view.findViewById(R.id.customer_profile_city_wrapper)
        addressField = view.findViewById(R.id.customer_profile_address_field)
        nameField = view.findViewById(R.id.customer_profile_name_field)
        phoneField = view.findViewById(R.id.customer_profile_phone_field)
        citySpinner = view.findViewById(R.id.customer_profile_city_view)
        citySpinner.setOnClickListener(this)
        areaSpinner = view.findViewById(R.id.customer_profile_area_view)
        areaSpinner.setOnClickListener(this)
        confirmButton = view.findViewById(R.id.customer_profile_confirm_button)
        confirmButton.setOnClickListener(this)

        getUserProfile()
    }

    private fun getUserProfile(){
        viewModel.getProfileLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getProfileLiveData().observe(viewLifecycleOwner, Observer{
            when(it.status){
                DataResource.Status.SUCCESS -> {
                    setDatatoViews(it.data)
                    newProfile = CustomerProfile()

                    newProfile!!.name = it.data.name
                    newProfile!!.phone = it.data.phone
                    newProfile!!.cityid = it.data.cityid
                    newProfile!!.cityname = it.data.cityname
                    newProfile!!.areaname = it.data.areaname
                    newProfile!!.areaid = it.data.areaid
                    newProfile!!.email = it.data.email
                    newProfile!!.address = it.data.address
                    newProfile!!.profilelevel = it.data.profilelevel

                    oldProfile = it.data
                    confirmButton.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    confirmButton.visibility = View.INVISIBLE
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    confirmButton.visibility = View.INVISIBLE
                }
            }
        })
        viewModel.getUserProfile()
    }

    private fun setDatatoViews(profile: CustomerProfile){
        selectedArea = Area(profile.areaname!!, profile.areaid!!)
        selectedCity = City(profile.cityname!!, profile.cityid!!)
        selectedAddress = profile.address!!

        areaSpinner.text = selectedArea!!.areaName
        citySpinner.text = selectedCity!!.cityName

        addressField.setText(selectedAddress)
        nameField.setText(profile.name)
        phoneField.setText(profile.phone)
        addressField.setText(profile.address)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.customer_profile_city_view -> {
                openDialogForSelecting(DIALOG_TYPE_CITY)
            }
            R.id.customer_profile_area_view -> {
                openDialogForSelecting(DIALOG_TYPE_AREA, cityid = selectedCity!!.cityId)
            }
            R.id.customer_profile_confirm_button -> {
                updateUserProfile()
            }
        }
    }

    private fun openDialogForSelecting(type: String, cityid: String = ""){
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialog_select_from_list)

        val searchView: SearchView = dialog.findViewById(R.id.dialog_searchview)
        val listView: ListView = dialog.findViewById(R.id.dialog_listview)
        val progressBar: ProgressBar = dialog.findViewById(R.id.dialog_progressbar)
        val header: TextView = dialog.findViewById(R.id.dialog_header)

        if(type == DIALOG_TYPE_CITY){
            header.text = "Choose Your City"
            viewModel.getCities().removeObservers(viewLifecycleOwner)

            viewModel.getCities().observe(viewLifecycleOwner, Observer {
                when(it.status){
                    CityResource.CityStatus.SUCCESS -> {
                        progressBar.visibility = View.INVISIBLE

                        searchView.setQuery("",false)
                        val adapter = CityListAdapter()
                        listView.adapter = adapter
                        adapter.setOriginalList(it.data)

                        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                adapter.getFilter().filter(newText)
                                return true
                            }
                        })

                        listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
                            if(adapter.getItem(position).cityId != "-1"){
                                onCitySelected(adapter.getItem(position))
                                dialog.dismiss()
                            }
                        }
                    }
                    CityResource.CityStatus.LOADING -> {
                    }
                    CityResource.CityStatus.ERROR -> {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }else if(type == DIALOG_TYPE_AREA){
            header.text = "Choose Your Area"
            viewModel.getAreaOfCity(cityid).removeObservers(viewLifecycleOwner)

            viewModel.getAreaOfCity(cityid).observe(viewLifecycleOwner, Observer {
                when(it.status){
                    CityResource.CityStatus.SUCCESS -> {
                        progressBar.visibility = View.INVISIBLE

                        searchView.setQuery("",false)
                        val adapter = AreaListAdapter()
                        listView.adapter = adapter
                        adapter.setOriginalList(it.data)

                        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                adapter.getFilter().filter(newText)
                                return true
                            }
                        })

                        listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
                            if(adapter.getItem(position).areaId != "-1"){
                                onAreaSelected(adapter.getItem(position))
                                dialog.dismiss()
                            }
                        }
                    }
                    CityResource.CityStatus.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    CityResource.CityStatus.ERROR -> {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun onCitySelected(city: City){
        if(selectedCity != null){
            if(selectedCity!!.cityName == city.cityName && selectedCity!!.cityId == city.cityId){
                return
            }
        }
        newProfile!!.cityid = city.cityId
        newProfile!!.cityname = city.cityName
        selectedCity = city
        citySpinner.text = city.cityName
        selectedArea = null
        areaSpinner.text= "Select Your Area"
        areaCard.visibility = View.VISIBLE
        checkAndShowButton()
    }

    private fun onAreaSelected(area: Area){
        selectedArea = area
        areaSpinner.text = area.areaName
        newProfile!!.areaid = area.areaId
        newProfile!!.areaname = area.areaName
        //addressCard.visibility = View.VISIBLE
        checkAndShowButton()
    }

    private fun updateUserProfile(){

        val name = nameField.text.toString().trim()
        val phone = phoneField.text.toString().trim()
        val address = addressField.text.toString().trim()

        newProfile!!.name = name
        newProfile!!.phone = phone
        newProfile!!.address = address

        if(name.isEmpty()){
            Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT).show()
            return
        }

        if(phone.isEmpty() || Helper.isPhoneNumber(phone)){
            Toast.makeText(context, "Please Enter Valid Number", Toast.LENGTH_SHORT).show()
            return
        }

        if(address.isEmpty()){
            Toast.makeText(context, "Please Enter Address", Toast.LENGTH_SHORT).show()
            return
        }

        if(oldProfile!!.equalsTo(newProfile)){
            Toast.makeText(context, "No changes", Toast.LENGTH_SHORT).show()
            return
        }

        val map: HashMap<String, Any> = HashMap()
        map["name"] = name
        map["phone"] = phone
        map["address"] = address
        map["cityname"] = selectedCity!!.cityName
        map["cityid"] = selectedCity!!.cityId
        map["areaname"] = selectedArea!!.areaName
        map["areaid"] = selectedArea!!.areaId
        map["profilelevel"] = Constants.PROFILE_LEVEL_1

        newProfile!!.profilelevel = Constants.PROFILE_LEVEL_1

        viewModel.updateUserProfile(map).addOnCompleteListener{
            if(it.isSuccessful){
                viewModel.saveProfileToLocal(map)
                oldProfile!!.name = newProfile!!.name
                oldProfile!!.phone = newProfile!!.phone
                oldProfile!!.cityid = newProfile!!.cityid
                oldProfile!!.cityname = newProfile!!.cityname
                oldProfile!!.areaname = newProfile!!.areaname
                oldProfile!!.areaid = newProfile!!.areaid
                oldProfile!!.email = newProfile!!.email
                oldProfile!!.address = newProfile!!.address
                oldProfile!!.profilelevel = newProfile!!.profilelevel
                showToast("Profile Updated")
            }
        }
    }

    private fun checkAndShowButton(){
        if(selectedCity != null && selectedArea != null){
            confirmButton.visibility = View.VISIBLE
        }else{
            confirmButton.visibility = View.INVISIBLE
        }
    }

    private fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}