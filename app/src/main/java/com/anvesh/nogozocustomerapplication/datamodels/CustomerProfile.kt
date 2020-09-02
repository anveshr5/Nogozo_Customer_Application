package com.anvesh.nogozocustomerapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CustomerProfile(
    var name: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var profilelevel: String? = "",
    var cityname: String? = "",
    var cityid: String? = "",
    var pincode: String? = "",
    var areaname: String? = "",
    var areaid: String? = "",
    var address: String? = ""){

    constructor(email: String, profilelevel: String) : this() {
        this.email = email
        this.profilelevel = profilelevel
    }

    fun equalsTo(other: Any?): Boolean{
        if(other == null)
            return false
        if(other is CustomerProfile){
            if(this.name == other.name
                && this.phone == other.phone
                && this.cityname == other.cityname
                && this.cityid == other.cityid
                && this.pincode == other.pincode
                && this.areaname == other.areaname
                && this.areaid == other.areaid
                && this.address == other.address){
                return true
            }
        }
        return false
    }
}