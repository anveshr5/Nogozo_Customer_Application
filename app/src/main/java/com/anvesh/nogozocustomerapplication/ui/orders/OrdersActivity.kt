package com.anvesh.nogozocustomerapplication.ui.orders

import android.os.Bundle
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.ui.BaseActivity
import com.anvesh.nogozocustomerapplication.ui.orders.customer.CustomerOrdersFragment

class OrdersActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val userType = intent.getStringExtra(USER_TYPE)

        if(userType == userType_CUSTOMER){
            startFragment(CustomerOrdersFragment())
        }
    }
    private fun startFragment(fragment: CustomerOrdersFragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.orders_container, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }
}