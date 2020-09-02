package com.anvesh.nogozocustomerapplication.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Auth {

    fun login(email: String, password: String): Task<AuthResult> {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String): Task<AuthResult>{
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
    }

}