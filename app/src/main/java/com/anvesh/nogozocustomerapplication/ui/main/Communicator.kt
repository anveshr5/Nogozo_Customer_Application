package com.anvesh.nogozocustomerapplication.ui.main

interface Communicator {
    fun onServiceSelected(serviceId: String, serviceName: String)

    fun onShopSelected(shopId: String, shopName: String, shopAddress: String?, shopAreaId: String)

    fun onGlobalSearch()

    fun setToolbarTitle(title: String){}
}