package com.anvesh.nogozocustomerapplication.util

import com.anvesh.nogozocustomerapplication.datamodels.Order

class OrderByDateTime: Comparator<Order> {
    override fun compare(o1: Order?, o2: Order?): Int {
        if(o1 == null || o2 == null)
            return 0
        if(o2.datetime.compareTo(o1.datetime) == 0){
            return o1.status.compareTo(o2.status)
        }
        return o2.datetime.compareTo(o1.datetime)
    }
}