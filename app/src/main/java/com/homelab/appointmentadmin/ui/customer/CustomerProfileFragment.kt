package com.homelab.appointmentadmin.ui.customer

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerProfileBinding

class CustomerProfileFragment : Fragment() {

    val args: CustomerProfileFragmentArgs by navArgs<CustomerProfileFragmentArgs>()
    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
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

        sharedViewModel.setUser(args.user)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            sharedViewModel = this@CustomerProfileFragment.sharedViewModel
            customerProfileFragment = this@CustomerProfileFragment
        }
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

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> maximizeCard()
                    1 -> minimizeCardForEdit()
                }
            }
        })
    }

    fun openEditTab() {
        binding.viewPager.currentItem = 1
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

            tabLayout.visibility = View.VISIBLE
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

            tabLayout.visibility = View.GONE
            cardEditBtnIcon.visibility = View.GONE
            cardBackBtnIcon.visibility = View.VISIBLE
            cardSaveBtnIcon.visibility = View.VISIBLE
        }
    }

}