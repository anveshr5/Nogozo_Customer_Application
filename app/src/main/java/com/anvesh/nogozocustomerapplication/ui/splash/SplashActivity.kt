package com.anvesh.nogozocustomerapplication.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.ui.BaseActivity
import com.anvesh.nogozocustomerapplication.ui.auth.AuthActivity
import com.anvesh.nogozocustomerapplication.ui.auth.AuthResource
import com.anvesh.nogozocustomerapplication.ui.main.MainActivity
import com.anvesh.nogozocustomerapplication.ui.userdetails.UserDetailsActivity
import com.anvesh.nogozocustomerapplication.util.Constants.PROFILE_LEVEL_0
import com.anvesh.nogozocustomerapplication.util.Constants.PROFILE_LEVEL_1
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class SplashActivity : BaseActivity(), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    //@Inject
    lateinit var sessionManager: SessionManager

    private lateinit var viewModel: SplashActivityViewModel
    private lateinit var viewPager: ViewPager
    private lateinit var dots: WormDotsIndicator
    private lateinit var splashImage: RelativeLayout

    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseApp.initializeApp(this)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        viewModel = ViewModelProvider(
            this@SplashActivity,
            ViewModelProvider.NewInstanceFactory()
        )[SplashActivityViewModel::class.java]
        viewPager = findViewById(R.id.splash_viewpager)
        dots = findViewById(R.id.splash_dotindicator)
        splashImage = findViewById(R.id.imgNogozoSplashImage)


        //setUpViewPager()

        findViewById<MaterialButton>(R.id.to_customer_login).setOnClickListener(this)

        Handler().postDelayed({
            subscribeObserver()
        }, 1000)

    }

//    private fun setUpViewPager(){
//        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
//        viewPager.adapter = viewPagerAdapter
//        dots.setViewPager(viewPager)
//    }

    private fun subscribeObserver() {

        CoroutineScope(IO).launch {
            val auth = viewModel.getCurrentUser()

            when (auth.status) {
                AuthResource.AuthStatus.AUTHENTICATED -> {
                    val userType = viewModel.getUserType()
                    if (userType == userType_CUSTOMER) {
                        val profileLevel = viewModel.getProfileLevel()
                        if (profileLevel == PROFILE_LEVEL_0) {
                            val i = Intent(this@SplashActivity, UserDetailsActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        } else if (profileLevel == PROFILE_LEVEL_1) {
                            val i = Intent(this@SplashActivity, MainActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        }
                    }
                }
                AuthResource.AuthStatus.NOT_AUTHENTICATED -> {
                    val i = Intent(this@SplashActivity, AuthActivity::class.java)
                    i.putExtra(USER_TYPE, userType_CUSTOMER)
                    startActivity(i)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    //splashImage.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.to_customer_login -> {

            }
            else -> {
                Toast.makeText(this, "Nothing Click", Toast.LENGTH_SHORT).show()
            }
        }
    }
}