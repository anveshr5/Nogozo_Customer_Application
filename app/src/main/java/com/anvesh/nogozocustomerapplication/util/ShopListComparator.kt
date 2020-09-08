package com.anvesh.nogozocustomerapplication.util

import android.util.Log
import com.anvesh.nogozocustomerapplication.datamodels.Shop

class ShopListComparator(val userAreaId: String): Comparator<Shop> {
    override fun compare(o1: Shop?, o2: Shop?): Int {
        if(o1 == null || o2 == null)
            return 0
        Log.d("areaaa", o1.shopAreaId + "   ${o2.shopAreaId}")
        Log.d("areaaa", o1.deliveryStatus + "   ${o2.deliveryStatus}")
        Log.d("areaaa",o1.shopName + "    ${o2.shopName}")

        return o2.shopName.compareTo(o1.shopName, true)



//        if (o1.shopAreaId!!.compareTo(userAreaId) == 0 && o2.shopAreaId!!.compareTo(userAreaId) == 0){
//            if (o1.deliveryStatus!!.compareTo(o2.deliveryStatus!!) == 0 ){
//                return o1.shopName.compareTo(o2.shopName)
//            } else {
//                return o1.deliveryStatus!!.length.compareTo(o2.deliveryStatus!!.length)
//            }
//        } else {
//            return o2.shopAreaId!!.toInt().compareTo(o1.shopAreaId!!.toInt())
//        }
    }
}