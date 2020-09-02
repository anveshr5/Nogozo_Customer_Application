package com.anvesh.nogozocustomerapplication.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class OrderNumber(val orderno: String): Parcelable{
    constructor(): this("")
}