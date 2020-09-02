package com.anvesh.nogozocustomerapplication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class City {
    @SerializedName("city_name")
    @Expose
    lateinit var cityName: String

    @SerializedName("city_id")
    @Expose
    lateinit var cityId: String

    constructor(cityName: String, cityId: String){
        this.cityId = cityId
        this.cityName = cityName
    }

    constructor()

}