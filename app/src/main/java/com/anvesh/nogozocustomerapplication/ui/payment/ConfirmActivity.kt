package com.anvesh.nogozocustomerapplication.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozocustomerapplication.ui.payment.customer.confirm.CustomerConfirmFragment
import com.anvesh.nogozocustomerapplication.util.Constants
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.ui.BaseActivity

class ConfirmActivity : BaseActivity() {

    lateinit var data: HashMap<String, Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val i = intent
        data = i.getSerializableExtra("order") as HashMap<String, Any>
        if(i.getStringExtra(Constants.USER_TYPE) == Constants.userType_CUSTOMER)
            initialFragment()
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.confirm_container, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    private fun initialFragment(){
        val f = CustomerConfirmFragment()
        val b = Bundle()
        b.putSerializable("order", data)
        f.arguments = b
        startFragment(f)
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }
}