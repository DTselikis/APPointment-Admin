package com.homelab.appointmentadmin.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerProfileBinding

class CustomerProfileFragment : Fragment() {

    private lateinit var binding: FragmentCustomerProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_profile, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {
        val pager = binding.viewPager
        val tabLayout = binding.tabLayout

        pager.adapter = CustomerPagerAdapter(this@CustomerProfileFragment)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.profile_contact_item)
                else -> getString(R.string.profile_edit_item)
            }
        }.attach()
    }

}