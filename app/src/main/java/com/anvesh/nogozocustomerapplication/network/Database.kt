package com.anvesh.nogozocustomerapplication.network

import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.datamodels.UserWallet
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult

class Database {


    fun uploadToken(token: String) {
        FirebaseDatabase.getInstance().reference.child("token")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(token)
    }

    fun getUserProfile(userType: String): DatabaseReference {

        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")
    }

    fun setUserProfile(userType: String, map: HashMap<String, Any>): Task<Void> {
        val ref = FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")

        return ref.updateChildren(map)
    }

    fun setUserProfileOnRegistered(userType: String, profile: CustomerProfile) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")

        val map: HashMap<String, Any> = HashMap()
        map["email"] = profile.email!!
        map["profilelevel"] = profile.profilelevel!!

        ref.updateChildren(map)
    }

    fun getCities(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("citylist")
    }

    fun getAreas(cityId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("arealist").child(cityId)
    }

    fun getAreaIds(cityId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("areaidlist").child(cityId)
    }

    fun getServices(): Query {
        return FirebaseDatabase.getInstance().reference
            .child("service").orderByChild("priority")
    }

    fun getShops(serviceId: String, cityId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("shops").child(cityId)
            .orderByChild("serviceid/$serviceId").equalTo(true)
    }

    fun getItems(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("items").child(shopId)
    }

    fun getShopAddress(shopid: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopid).child("address")
    }

    fun getShopAreaId(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("profile").child("areaid")
    }

    fun getShopStatus(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("status")
    }

    fun getShopBusinessType(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("/users/shop/${shopId}/profile/homebusiness")
    }

    fun getFare(
        price: String,
        cityId: String,
        areaId: String,
        shopAreaId: String
    ): Task<HttpsCallableResult> {
        val data: HashMap<String, String> = HashMap()
        data["itemprice"] = price
        data["areaid"] = areaId
        data["cityid"] = cityId
        data["shopareaid"] = shopAreaId

        return FirebaseFunctions.getInstance()
            .getHttpsCallable("fare")
            .call(data)
    }

    fun createOrder(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("orders").push()
    }

    fun getCurrentOrders(userType: String, userId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(userId).child("orders").orderByValue().equalTo("current")
    }

    fun getPastOrder(userType: String, userId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(userId).child("orders").orderByValue().equalTo("history")
    }

    fun getOrderDetails(orderId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("orders").child(orderId)
    }


    fun searchItemsinCity(query: String, cityId: String): Task<HttpsCallableResult> {
        val data: HashMap<String, String> = HashMap()
        data["query"] = query
        data["cityid"] = cityId
        return FirebaseFunctions.getInstance()
            .getHttpsCallable("search")
            .call(data)
    }

    private fun initialiseWallet() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseDatabase.getInstance().getReference("/users-wallet/$userId")
        val newWallet: UserWallet = UserWallet(userId, userId, "0")
        db.setValue(newWallet)
    }

    fun getOrderNumber(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users").child("shop").child(shopId)
            .child("ordernumber")
    }

    fun getDeliveryStatus(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("profile").child("deliverystatus")
    }

    fun getDeliveryCharges(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users")
            .child("shop").child(shopId).child("profile")
            .child("deliverycharges")
    }

    fun getDeliveryMinOrder(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users")
            .child("shop").child(shopId).child("profile")
            .child("deliveryminorder")
    }

    fun getShopPincode(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("users")
            .child("shop").child(shopId).child("profile")
            .child("pincode")
    }
}