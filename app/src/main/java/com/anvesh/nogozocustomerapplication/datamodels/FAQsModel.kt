package com.anvesh.nogozocustomerapplication.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FAQsModel(val question: String, val answer: String): Parcelable{
    constructor(): this("","")
}