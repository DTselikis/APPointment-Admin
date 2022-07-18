package com.homelab.appointmentadmin.ui.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.ActivityCustomerProfileBinding
import kotlin.math.max

class CustomerProfileActivity : AppCompatActivity() {

    val args: CustomerProfileActivityArgs by navArgs<CustomerProfileActivityArgs>()
    private val sharedViewModel: CustomerProfileSharedViewModel by viewModels()
    private lateinit var binding: ActivityCustomerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel.setUser(args.user)
        binding.lifecycleOwner = this
        binding.sharedViewModel = this@CustomerProfileActivity.sharedViewModel
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

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when (destination.id) {
                R.id.profile_contact_item -> maximizeCard()
                R.id.customerProfileEditFragment -> minimizeCardForEdit()
            }
        }

    }

    fun navigateToProfileEdit() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.profile_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.action_profile_contact_item_to_customerProfileEditFragment)
    }

    fun exitProfileEdit() {
        onBackPressed()
    }

    fun pressSaveBtn() {
        sharedViewModel.pressSaveBtn()
    }

    private fun maximizeCard() {
        TransitionManager.beginDelayedTransition(
            binding.photoCard as ViewGroup,
            AutoTransition()
        )
        binding.apply {
            photoCard.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    270f,
                    resources.displayMetrics
                ).toInt()
            }

            customerProfilePic.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    120f,
                    resources.displayMetrics
                ).toInt()
                width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    120f,
                    resources.displayMetrics
                ).toInt()

                horizontalBias = 0.5f
            }

            customerNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = customerProfilePic.id
                startToStart = customerProfilePic.id
                endToEnd = customerProfilePic.id
                bottomToBottom = photoCard.id

                horizontalBias = 0.5f
                verticalBias = 0.2f
            }

            navView.visibility = View.VISIBLE
            cardEditBtnIcon.visibility = View.VISIBLE
            cardBackBtnIcon.visibility = View.GONE
            cardSaveBtnIcon.visibility = View.GONE
        }

    }

    private fun minimizeCardForEdit() {
        TransitionManager.beginDelayedTransition(
            binding.photoCard as ViewGroup,
            AutoTransition()
        )
        binding.apply {
            photoCard.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    40f,
                    resources.displayMetrics
                ).toInt()
            }

            customerProfilePic.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    35f,
                    resources.displayMetrics
                ).toInt()
                width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    35f,
                    resources.displayMetrics
                ).toInt()

                horizontalBias = 0.3f
            }

            customerNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToTop = customerProfilePic.id
                bottomToBottom = customerProfilePic.id
                endToStart = cardSaveBtnIcon.id
                startToEnd = customerProfilePic.id

                horizontalBias = 0.25f
                verticalBias = 0.5f
            }

            navView.visibility = View.GONE
            cardEditBtnIcon.visibility = View.GONE
            cardBackBtnIcon.visibility = View.VISIBLE
            cardSaveBtnIcon.visibility = View.VISIBLE
        }
    }
}