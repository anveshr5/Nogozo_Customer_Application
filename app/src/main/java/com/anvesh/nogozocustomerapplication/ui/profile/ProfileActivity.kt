package com.anvesh.nogozocustomerapplication.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.ui.BaseActivity
import com.anvesh.nogozocustomerapplication.ui.profile.customer.CustomerProfileFragment
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userType = intent.getStringExtra(USER_TYPE)
        if(userType == userType_CUSTOMER)
            startFragment(CustomerProfileFragment())
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.profile_container, fragment)
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