package com.homelab.appointmentadmin.ui.hub.create

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCreateCustomerBinding

class CreateCustomerFragment : Fragment() {

    private val viewModel: CreateCustomerViewModel by viewModels()
    private lateinit var binding: FragmentCreateCustomerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_customer, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CreateCustomerFragment.viewModel
            customerProfileEditFragment = this@CreateCustomerFragment
        }
    }

    fun saveCustomer() {
        if (isValid()) {
        }
    }

    private fun isValid(): Boolean {
        return if (viewModel.firstname.value.isNullOrBlank() && viewModel.lastname.value.isNullOrBlank()) {
            binding.apply {
                firstname.error = getString(R.string.one_field_requited_err_msg)
                lastname.error = getString(R.string.one_field_requited_err_msg)
            }
            false
        } else {
            true
        }
    }

}