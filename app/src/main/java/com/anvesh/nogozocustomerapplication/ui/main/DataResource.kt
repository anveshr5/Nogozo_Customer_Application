package com.anvesh.nogozocustomerapplication.ui.main

import androidx.annotation.NonNull
import androidx.annotation.Nullable

class DataResource<T> {

    @Nullable
    var status: Status

    @Nullable
    var data: T

    @Nullable
    var message: String

    constructor(@NonNull Status: Status, @Nullable data: T, @Nullable message: String){
        this.status =Status
        this.data = data
        this.message = message
    }

    companion object {
        fun <T> success(data: T): DataResource<T> {
            return DataResource(
                Status.SUCCESS,
                data,
                "success"
            )
        }

        fun <T> error(msg: String): DataResource<T> {
            return DataResource(
                Status.ERROR,
                null as T,
                msg
            )
        }

        fun <T> loading(): DataResource<T> {
            return DataResource(
                Status.LOADING,
                null as T,
                "loading"
            )
        }

        fun <T> emptyResult(): DataResource<T> {
            return DataResource(
                Status.NO_RESULT,
                null as T,
                "No Result Found"
            )
        }
    }
    enum class Status{SUCCESS, ERROR, LOADING, NO_RESULT}
}