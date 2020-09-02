package com.anvesh.nogozocustomerapplication

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.network.Auth
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.auth.AuthResource
import com.anvesh.nogozocustomerapplication.ui.splash.SplashActivity
import com.anvesh.nogozocustomerapplication.util.Constants.ADDRESS
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_ID
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.CITY_ID
import com.anvesh.nogozocustomerapplication.util.Constants.CITY_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.EMAIL
import com.anvesh.nogozocustomerapplication.util.Constants.NAME
import com.anvesh.nogozocustomerapplication.util.Constants.PHONE
import com.anvesh.nogozocustomerapplication.util.Constants.PROFILE_LEVEL
import com.anvesh.nogozocustomerapplication.util.Constants.PROFILE_LEVEL_0
import com.anvesh.nogozocustomerapplication.util.Constants.PROFILE_LEVEL_1
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
//@Inject
 constructor(
    ) {

    private val database: Database = Database()
    private val preferences: SharedPreferences = SplashActivity.sharedPreferences
    private val editPreferences: SharedPreferences.Editor = preferences.edit()


    val currentSessionData: HashMap<String, String> by lazy {
        val data:HashMap<String, String> = HashMap()
        data[CITY_ID] = getCityId()
        data[CITY_NAME] = getCityName()
        data[AREA_ID] = getAreaId()
        data[AREA_NAME] = getAreaName()
        return@lazy data
    }

    fun getCurrentUser(): AuthResource {
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null)
            return AuthResource.authenticated()
        return AuthResource.notAuthenticated()
    }

    fun getUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun logout(){
        editPreferences.clear().apply()
        FirebaseAuth.getInstance().signOut()
    }

    fun getUserType(): String{
        return preferences.getString(USER_TYPE, "-1")!!
    }

    fun getCityId(): String{
        return preferences.getString(CITY_ID,"-1")!!
    }

    fun getCityName(): String{
        return preferences.getString(CITY_NAME, "")!!
    }

    fun getAreaId(): String{
        return preferences.getString(AREA_ID,"-1")!!
    }

    fun getAreaName(): String{
        return preferences.getString(AREA_NAME, "")!!
    }

    fun getProfileLevel(): String{
        return preferences.getString(PROFILE_LEVEL, PROFILE_LEVEL_0)!!
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return Auth().login(email, password)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        return Auth().register(email, password)
    }

    fun getUserProfile(): DatabaseReference{
        return database.getUserProfile(getUserType())
    }

    fun getUserName(): String{
        return preferences.getString(NAME,"")!!
    }
    fun getUserPhone(): String{
        return preferences.getString(PHONE,"")!!
    }
    fun getUserAddress(): String{
        return preferences.getString(ADDRESS,"")!!
    }

    fun getUserPincode(): String{
        return preferences.getString("Pincode", "")!!
    }

    fun saveProfileToLocal(profile: CustomerProfile){
        CoroutineScope(Dispatchers.Default).launch {
            editPreferences.putString(PROFILE_LEVEL, PROFILE_LEVEL_1).apply()
            editPreferences.putString(EMAIL, profile.email).apply()
            editPreferences.putString(CITY_ID, profile.cityid).apply()
            editPreferences.putString(CITY_NAME, profile.cityname).apply()
            editPreferences.putString(AREA_ID, profile.areaid).apply()
            editPreferences.putString(AREA_NAME, profile.areaname).apply()
            editPreferences.putString(NAME, profile.name).apply()
            editPreferences.putString(ADDRESS, profile.address).apply()
            editPreferences.putString("Pincode",profile.pincode).apply()
            editPreferences.putString(PHONE, profile.phone).apply()
            currentSessionData[AREA_NAME] = profile.areaname!!
            currentSessionData[AREA_ID] = profile.areaid!!
            currentSessionData[CITY_NAME] = profile.cityname!!
            currentSessionData[CITY_ID] = profile.cityid!!
        }
    }

    fun saveCustomerProfileToLocal(map: HashMap<String, Any>){
        CoroutineScope(Dispatchers.Default).launch {
            editPreferences.putString(PROFILE_LEVEL, PROFILE_LEVEL_1).apply()
            editPreferences.putString(CITY_ID, map["cityid"] as String).apply()
            editPreferences.putString(CITY_NAME, map["cityname"] as String).apply()
            editPreferences.putString(AREA_ID, map["areaid"] as String).apply()
            editPreferences.putString(AREA_NAME, map["areaname"] as String).apply()
            editPreferences.putString(NAME, map["name"] as String).apply()
            editPreferences.putString(ADDRESS, map["address"] as String).apply()
            editPreferences.putString(PHONE, map["phone"] as String).apply()
            editPreferences.putString("Pincode",map["pincode"] as String).apply()

            currentSessionData[AREA_NAME] = map["areaname"] as String
            currentSessionData[AREA_ID] = map["areaid"] as String
            currentSessionData[CITY_NAME] = map["cityname"] as String
            currentSessionData[CITY_ID] = map["cityid"] as String
        }
    }
    fun saveOnRegistered(email: String, userType: String){
        CoroutineScope(Dispatchers.Default).launch{
            editPreferences.putString(EMAIL, email).apply()
            editPreferences.putString(PROFILE_LEVEL, PROFILE_LEVEL_0).apply()
            editPreferences.putString(USER_TYPE, userType).apply()
            database.setUserProfileOnRegistered(userType, CustomerProfile(email, PROFILE_LEVEL_0))
        }
    }

    fun uploadToken(token: String){
        database.uploadToken(token)
    }

    fun saveOnLogged(email: String, userType: String){
        editPreferences.putString(EMAIL, email).apply()
        editPreferences.putString(USER_TYPE, userType).apply()
    }
}