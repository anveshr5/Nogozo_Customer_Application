package com.anvesh.nogozocustomerapplication.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserWallet(val userId: String,val transactionId: String, val walletValue: String):Parcelable {
    constructor():this("","","")
}