package com.anvesh.nogozocustomerapplication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AreaId {
    @SerializedName("area_id")
    @Expose
    lateinit var areaId: String

    @SerializedName("area_num")
    @Expose
    lateinit var areaNum: String

    constructor(areaId: String, areaNum: String){
        this.areaId = areaId
        this.areaNum = areaNum
    }

    constructor()
}