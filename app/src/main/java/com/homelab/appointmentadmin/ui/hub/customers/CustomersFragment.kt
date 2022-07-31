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
            usersRv.adapter = userAdapter
        }

        viewModel.fetchUsersFromDB()
        observeForResult()
    }

    fun navigate(user: User) {
        val action =
            CustomersFragmentDirections.actionCustomersFragmentToCustomerProfileFragment(user)
        findNavController().navigate(action)
    }

    private fun observeForResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>(USER_NAV_KEY)
            ?.observe(viewLifecycleOwner) { updatedUser ->
                val position = viewModel.updateUser(updatedUser)
                userAdapter.notifyItemChanged(position)
            }
    }

    fun createNewCustomer() {
        findNavController().navigate(R.id.action_customersFragment_to_createCustomerFragment)
    }

}