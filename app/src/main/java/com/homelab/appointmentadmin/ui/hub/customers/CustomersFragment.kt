package com.homelab.appointmentadmin.ui.hub.customers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.NEW_USER_NAV_KEY
import com.homelab.appointmentadmin.data.USER_NAV_KEY
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.databinding.FragmentCustomersBinding

class CustomersFragment : Fragment() {

    private lateinit var binding: FragmentCustomersBinding
    private val viewModel: CustomersViewModel by viewModels()

    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customers, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter(this@CustomersFragment)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomersFragment.viewModel
            fragmentCustomers = this@CustomersFragment
            usersRv.adapter = userAdapter
        }

        viewModel.fetchUsersFromDB()
        observeForUserModifications()
        observeForNewUser()
    }

    fun navigateToEditCustomer(user: User) {
        val action =
            CustomersFragmentDirections.actionCustomersFragmentToCustomerProfileFragment(user)
        findNavController().navigate(action)
    }

    private fun observeForUserModifications() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>(USER_NAV_KEY)
            ?.observe(viewLifecycleOwner) { updatedUser ->
                val position = viewModel.updateUser(updatedUser)
                userAdapter.notifyItemChanged(position)
            }
    }

    private fun observeForNewUser() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>(
            NEW_USER_NAV_KEY
        )?.observe(viewLifecycleOwner) { newUser ->
            viewModel.insertUser(newUser)
        }
    }

    fun navigateToCreateNewCustomer() {
        findNavController().navigate(R.id.action_customersFragment_to_createCustomerFragment)
    }

    fun toggleFilters() {
        if (binding.filters.visibility == View.GONE) {
            binding.apply {
                filterBtn.animate().rotation(-90f).start()
                filters.visibility = View.VISIBLE
                filters.animate().alpha(1f).start()
            }
        } else {
            binding.apply {
                filterBtn.animate().rotation(0f).start()
                val hideAnimation =
                    AnimationUtils.loadAnimation(filters.context, R.anim.filter_btn_hide_anim)
                hideAnimation.apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {
                            filters.apply {
                                visibility = View.GONE
                                alpha = 0f
                            }
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}
                    })
                }
                filters.startAnimation(hideAnimation)
            }
        }
    }

    fun showRegisteredCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.selected_filter,
                    requireActivity().theme
                )
            )
        }
        viewModel.filterUsers(registered = true)
    }

    fun showUnregisteredCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.selected_filter,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
        }
        viewModel.filterUsers(registered = false)
    }

    fun showAllCustomers() {
        binding.apply {
            showAllFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.selected_filter,
                    requireActivity().theme
                )
            )
            showRegisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
            showUnregisteredFilter.setCardBackgroundColor(
                resources.getColor(
                    R.color.white,
                    requireActivity().theme
                )
            )
        }
        viewModel.resetFilter()
    }

    fun toggleSearch() {
        binding.apply {
            if (filters.visibility == View.VISIBLE) toggleFilters()

            searchText.visibility =
                if (searchText.visibility == View.GONE) View.VISIBLE else View.GONE
        }

    }
}