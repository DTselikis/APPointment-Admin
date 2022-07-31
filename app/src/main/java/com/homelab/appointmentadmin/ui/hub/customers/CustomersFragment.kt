package com.homelab.appointmentadmin.ui.hub.customers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if (binding.showAllFilter.visibility == View.GONE) {
            binding.apply {
                filterBtn.animate().rotation(-90f).start()
                showAllFilter.visibility = View.VISIBLE
                showAllFilter.animate().alpha(1f).start()
                showRegisteredFilter.visibility = View.VISIBLE
                showRegisteredFilter.animate().alpha(1f).start()
                showUnregisteredFilter.visibility = View.VISIBLE
                showUnregisteredFilter.animate().alpha(1f).start()
            }
        } else {
            binding.apply {
                filterBtn.animate().rotation(0f).start()
                showAllFilter.animate().alpha(0f).start()
                showAllFilter.visibility = View.GONE
                showRegisteredFilter.animate().alpha(0f).start()
                showRegisteredFilter.visibility = View.GONE
                showUnregisteredFilter.animate().alpha(0f).start()
                showUnregisteredFilter.visibility = View.GONE
            }

        }
    }

}