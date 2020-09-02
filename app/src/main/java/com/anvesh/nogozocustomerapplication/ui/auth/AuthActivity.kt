package com.anvesh.nogozocustomerapplication.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozocustomerapplication.ui.auth.customer.CustomerAuthFragment
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.ui.BaseActivity

class AuthActivity : BaseActivity() {

    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        userType = intent.getStringExtra(USER_TYPE)

        if(userType.equals(userType_CUSTOMER))
            startFragment(CustomerAuthFragment())

    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.auth_container, fragment)
        ft.commit()
    }
}