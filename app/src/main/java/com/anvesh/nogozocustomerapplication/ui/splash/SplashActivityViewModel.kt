package com.anvesh.nogozocustomerapplication.ui.splash

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.auth.AuthResource
import javax.inject.Inject

class SplashActivityViewModel
    //@Inject
    //constructor(
      //  val sessionManager: SessionManager
    //)
    : ViewModel() {

    val sessionManager: SessionManager = SessionManager()

    suspend fun getCurrentUser(): AuthResource {
        return sessionManager.getCurrentUser()
    }

    suspend fun getUserType(): String {
        return sessionManager.getUserType()
    }

    suspend fun getProfileLevel(): String {
        return sessionManager.getProfileLevel()
    }
}