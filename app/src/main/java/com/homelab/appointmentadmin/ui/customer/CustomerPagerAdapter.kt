package com.homelab.appointmentadmin.ui.customer

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.homelab.appointmentadmin.ui.customer.contact.CustomerContactFragment
import com.homelab.appointmentadmin.ui.customer.edit.CustomerProfileEditFragment

class CustomerPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CustomerContactFragment()
            else -> CustomerProfileEditFragment()
        }
    }
}