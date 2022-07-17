package com.homelab.appointmentadmin.ui.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.ActivityCustomerProfileBinding

class CustomerProfileActivity : AppCompatActivity() {

    val args: CustomerProfileActivityArgs by navArgs<CustomerProfileActivityArgs>()
    private val sharedViewModel: CustomerProfileSharedViewModel by viewModels()
    private lateinit var binding: ActivityCustomerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel.user = args.user
        binding.user = args.user
        binding.customerProfileActivity = this@CustomerProfileActivity

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

    fun navigateToProfileEdit() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.profile_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.action_profile_contact_item_to_customerProfileEditFragment)
    }
}