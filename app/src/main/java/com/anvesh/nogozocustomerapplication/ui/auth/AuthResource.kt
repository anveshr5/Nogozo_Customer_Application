package com.anvesh.nogozocustomerapplication.ui.auth

import androidx.annotation.NonNull
import androidx.annotation.Nullable

class AuthResource {

    @NonNull
    var status: AuthStatus

    @Nullable
    var message: String = ""

    constructor(@NonNull status: AuthStatus){
        this.status = status
    }

    constructor(@NonNull status: AuthStatus, @Nullable message: String){
        this.status = status
        this.message = message
    }

    enum class AuthStatus{NOT_AUTHENTICATED, AUTHENTICATED}

    companion object {
        fun authenticated(): AuthResource {
            return AuthResource(AuthStatus.AUTHENTICATED)
        }

        fun notAuthenticated(): AuthResource {
            return AuthResource(AuthStatus.NOT_AUTHENTICATED)
        }
    }
}