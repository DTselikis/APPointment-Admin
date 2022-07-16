package com.homelab.appointmentadmin.ui.customer.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.ActivityCustomerProfileBinding

class CustomerProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView

        // Workaround for the FragmentContainerView bug
        // https://issuetracker.google.com/issues/142847973
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.profile_nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }
}