package com.anvesh.nogozocustomerapplication.ui.orders.customer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.anvesh.nogozocustomerapplication.ui.orders.customer.current.CustomerCurrentOrdersFragment
import com.anvesh.nogozocustomerapplication.ui.orders.customer.past.CustomerPastOrdersFragment

class CustomerOrdersViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val currentOrdersFragment: Fragment = CustomerCurrentOrdersFragment()
    private val pastOrdersFragment: Fragment = CustomerPastOrdersFragment()

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                currentOrdersFragment
            }
            1 -> {
                pastOrdersFragment
            }
            else -> {
                return Fragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> {
                "Current Orders"
            }
            1 -> {
                "Past Orders"
            }
            else -> {
                return ""
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}