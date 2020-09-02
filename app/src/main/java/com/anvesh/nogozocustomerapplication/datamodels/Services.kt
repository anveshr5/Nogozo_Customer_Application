package com.anvesh.nogozocustomerapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Services(
    var serviceId: String? = "-1",
    var servicename: String? = "",
    var priority: Int? = 100
): Serializable {

}