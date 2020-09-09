package com.anvesh.nogozocustomerapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Shop {
    lateinit var shopName: String

    lateinit var shopId: String

    var imageUrl: String? = null

    var shopAddress: String? = null

    var homebusiness: String? = null

    var shopCurrentStatus: String? = null

    var deliveryStatus: String? = null

    var shopAreaId: String? = null

    constructor(shopName: String, shopId: String, imageUrl: String?, shopCurrentStatus: String?, shopAreaId: String?,deliveryStatus: String?){
        this.shopId = shopId
        this.shopName = shopName
        this.imageUrl = imageUrl
        this.shopCurrentStatus = shopCurrentStatus
        this.shopAreaId = shopAreaId
        this.deliveryStatus = deliveryStatus
    }

    constructor()
}