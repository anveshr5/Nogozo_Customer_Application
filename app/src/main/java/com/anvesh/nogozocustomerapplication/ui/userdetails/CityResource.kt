package com.anvesh.nogozocustomerapplication.ui.userdetails

import androidx.annotation.NonNull
import androidx.annotation.Nullable

class CityResource<T> {

    @Nullable
    var status: CityStatus

    @Nullable
    var data: T

    @Nullable
    var message: String

    constructor(@NonNull Status: CityStatus, @Nullable data: T, @Nullable message: String){
        this.status =Status
        this.data = data
        this.message = message
    }

    companion object {
        fun <T> success(data: T): CityResource<T> {
            return CityResource(CityStatus.SUCCESS, data, "success")
        }

        fun <T> error(msg: String): CityResource<T> {
            return CityResource(CityStatus.ERROR, null as T, msg)
        }

        fun <T> loading(): CityResource<T> {
            return CityResource(CityStatus.LOADING, null as T, "loading")
        }
    }
    enum class CityStatus{SUCCESS, ERROR, LOADING}
}