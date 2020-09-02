package com.anvesh.nogozocustomerapplication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Area {
    @SerializedName("area_name")
    @Expose
    lateinit var areaName: String

    @SerializedName("area_id")
    @Expose
    lateinit var areaId: String

    constructor(areaName: String, areaId: String){
        this.areaId = areaId
        this.areaName = areaName
    }

    constructor()
}