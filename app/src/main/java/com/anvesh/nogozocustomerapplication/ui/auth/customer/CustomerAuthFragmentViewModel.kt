package com.anvesh.nogozocustomerapplication.ui.auth.customer

import androidx.lifecycle.ViewModel
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference

class CustomerAuthFragmentViewModel
//@Inject
//constructor(
//  private val sessionManager: SessionManager)
    : ViewModel() {

    val sessionManager = SessionManager()

    fun login(email: String, password: String): Task<AuthResult> {
        return sessionManager.login(email, password)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        return sessionManager.register(email, password)
    }

    fun getUserProfile(): DatabaseReference {
        return sessionManager.getUserProfile()
    }

    fun saveProfileToLocal(profile: CustomerProfile) {
        sessionManager.saveProfileToLocal(profile)
    }

    fun saveOnRegistered(email: String) {
        sessionManager.saveOnRegistered(email, userType_CUSTOMER)
    }

    fun saveOnLogged(email: String) {
        sessionManager.saveOnLogged(email, userType_CUSTOMER)
    }

    fun uploadToken(token: String) {
        sessionManager.uploadToken(token)
    }
}