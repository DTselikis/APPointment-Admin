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
import com.homelab.appointmentadmin.data.User
import com.homelab.appointmentadmin.databinding.FragmentCustomersBinding

class CustomersFragment : Fragment() {

    private lateinit var binding: FragmentCustomersBinding
    private val viewModel: CustomersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customers, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomersFragment.viewModel
            usersRv.adapter = UserAdapter(this@CustomersFragment)
        }

        viewModel.fetchUsersFromDB()
    }

    fun navigate(user: User) {
        val action =
            CustomersFragmentDirections.actionCustomersFragmentToCustomerProfileFragment(user)
        findNavController().navigate(action)
    }

    private fun observeForResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>("user")?.observe(viewLifecycleOwner) { updatedUser ->
            viewModel.updateUser(updatedUser)
        }
    }

}