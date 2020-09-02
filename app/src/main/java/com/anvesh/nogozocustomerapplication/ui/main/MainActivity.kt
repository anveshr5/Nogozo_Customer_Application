package com.anvesh.nogozocustomerapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.anvesh.nogozocustomerapplication.ui.main.customer.itemsInShop.ItemsInShopFragment
import com.anvesh.nogozocustomerapplication.ui.main.customer.search.GlobalSearchFragment
import com.anvesh.nogozocustomerapplication.ui.main.customer.services.CustomerServicesFragment
import com.anvesh.nogozocustomerapplication.ui.main.customer.shops.ShopListFragment
import com.anvesh.nogozocustomerapplication.ui.orders.OrdersActivity
import com.anvesh.nogozocustomerapplication.ui.profile.ProfileActivity
import com.anvesh.nogozocustomerapplication.util.Constants
import com.anvesh.nogozocustomerapplication.util.Constants.AREA_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SERVICE_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SERVICE_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ADDRESS
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_ID
import com.anvesh.nogozocustomerapplication.util.Constants.SHOP_NAME
import com.anvesh.nogozocustomerapplication.util.Constants.USER_TYPE
import com.anvesh.nogozocustomerapplication.util.Constants.userType_CUSTOMER
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.SessionManager
import com.anvesh.nogozocustomerapplication.datamodels.CustomerProfile
import com.anvesh.nogozocustomerapplication.ui.BaseActivity
import com.anvesh.nogozocustomerapplication.ui.contactus.ContactUsFaqActivity
import com.anvesh.nogozocustomerapplication.ui.splash.SplashActivity

class MainActivity : BaseActivity(), Communicator, View.OnClickListener {

    //@Inject
    lateinit var sessionManager: SessionManager


    private lateinit var drawerButton: ImageButton
    private lateinit var drawer: Drawer

    private lateinit var usernameHeader: TextView
    private lateinit var customerEmailHeader: TextView
    private lateinit var customerPhoneHeader: TextView

    private var userProfile: CustomerProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        sessionManager = SessionManager()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerButton = findViewById(R.id.header_profile)
        drawerButton.setOnClickListener(this)


        val userType = intent.getStringExtra(USER_TYPE)
        if (userType == userType_CUSTOMER) {
            buildCustomerDrawer()
            getUserProfile()
            startFragment(CustomerServicesFragment(this))
        }
    }

    private fun getUserProfile() {
        FirebaseDatabase.getInstance().reference.child("users").child(userType_CUSTOMER)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile").
            addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userProfile = snapshot.getValue(CustomerProfile::class.java)
                    setUpCustomerHeaderViews()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    fun setUpCustomerHeaderViews(){
        usernameHeader = drawer.header!!.findViewById(R.id.drawerHeaderUsername)
        customerEmailHeader = drawer.header!!.findViewById(R.id.drawerHeaderEmail)
        customerPhoneHeader = drawer.header!!.findViewById(R.id.drawerHeaderPhoneNumber)
        usernameHeader.text = userProfile!!.name
        customerEmailHeader.text = userProfile!!.email
        customerPhoneHeader.text = userProfile!!.phone
    }

    private fun buildCustomerDrawer() {
        //        account header
        drawer = DrawerBuilder()
            .withActivity(this)
            .withTranslucentNavigationBar(true)
            .withDrawerGravity(GravityCompat.END).withHeader(R.layout.header_view_customer_navigation_drawer)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(1).withName("Orders").withSelectable(false)
                    .withTextColor(
                        ContextCompat.getColor(this, R.color.colorPrimaryLight)
                    ).withIcon(R.drawable.ic_orders),
                PrimaryDrawerItem().withIdentifier(2).withName("Profile").withSelectable(false)
                    .withTextColor(
                        ContextCompat.getColor(this, R.color.colorPrimaryLight)
                    ).withIcon(R.drawable.ic_profile),
                PrimaryDrawerItem().withIdentifier(3).withName("Contact Us").withSelectable(false)
                    .withTextColor(
                        ContextCompat.getColor(this, R.color.colorPrimaryLight)
                    ).withIcon(R.drawable.ic_contact_us),
                DividerDrawerItem().withIdentifier(4),
                SecondaryDrawerItem().withIdentifier(5).withName("Sign Out").withSelectable(false)
                    .withTextColor(
                        ContextCompat.getColor(this, R.color.red)
                    ).withIcon(R.drawable.ic_sign_out)
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        1 -> {
                            val i = Intent(this@MainActivity, OrdersActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            startActivity(i)
                        }
                        98 -> {
                            val i = Intent(this@MainActivity, OrdersActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            i.putExtra(Constants.ORDER_TYPE, Constants.CURRENT_ORDER)
                            startActivity(i)
                        }
                        99 -> {
                            val i = Intent(this@MainActivity, OrdersActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            i.putExtra(Constants.ORDER_TYPE, Constants.PAST_ORDER)
                            startActivity(i)
                        }
                        2 -> {
                            val i = Intent(this@MainActivity, ProfileActivity::class.java)
                            i.putExtra(USER_TYPE, userType_CUSTOMER)
                            startActivity(i)
                        }
                        3 -> {
                            val i = Intent(this@MainActivity, ContactUsFaqActivity::class.java)
                            startActivity(i)
                        }
                        5 -> {
                            sessionManager.logout()
                            val i = Intent(this@MainActivity, SplashActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                        }
                    }
                    drawer.closeDrawer()
                    return true
                }
            })
            .build()
        drawer.setSelection(-1, false)
        }

    private fun startFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_container, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }

    override fun onServiceSelected(serviceId: String, serviceName: String) {
        //start fragment to show shops list
        val f = ShopListFragment(this)
        val b = Bundle()
        b.putString(SERVICE_ID, serviceId)
        b.putString(SERVICE_NAME, serviceName)
        f.arguments = b
        startFragment(f)
    }

    override fun onShopSelected(
        shopId: String,
        shopName: String,
        shopAddress: String?,
        shopAreaId: String
    ) {
        //start fragment to show item list with specefic shopid
        val f = ItemsInShopFragment(this)
        val b = Bundle()
        b.putString(SHOP_ID, shopId)
        b.putString(SHOP_NAME, shopName)
        b.putString(SHOP_ADDRESS, shopAddress)
        b.putString(AREA_ID, shopAreaId)
        f.arguments = b
        startFragment(f)
    }

    override fun onGlobalSearch() {
        startFragment(GlobalSearchFragment(this))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.header_profile -> {
                drawer.openDrawer()
            }
        }
    }
}