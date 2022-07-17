package com.homelab.appointmentadmin.ui.customer.edit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerContactBinding
import com.homelab.appointmentadmin.databinding.FragmentCustomerProfileEditBinding
import com.homelab.appointmentadmin.ui.customer.contact.CustomerProfileSharedViewModel

class CustomerProfileEditFragment : Fragment() {

    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()
    private val viewModel: CustomerProfileEditViewModel by viewModels {
        CustomerProfileEditViewModelFactory(sharedViewModel.user)
    }
    private lateinit var binding: FragmentCustomerProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_customer_profile_edit, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomerProfileEditFragment.viewModel
            customerProfileEditFragment = this@CustomerProfileEditFragment
        }

        observeForEdits()
    }

    private fun observeForEdits() {
        viewModel.firstname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.lastname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.nickname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.phone.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.email.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.fbName.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
    }
}