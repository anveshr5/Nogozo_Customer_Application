package com.anvesh.nogozocustomerapplication.ui.userdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.ui.BaseActivity
import com.anvesh.nogozocustomerapplication.ui.userdetails.customer.CustomerDetailsFragment
import javax.inject.Inject

class UserDetailsActivity : BaseActivity() {

    //@Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {

        sessionManager =SessionManager()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val i = intent
        if(i.getStringExtra(USER_TYPE) == userType_CUSTOMER)
            startFragment(CustomerDetailsFragment())
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.userdetails_container, fragment)
        ft.commit()
    }
}