package com.homelab.appointmentadmin.ui.hub.create

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.data.NEW_USER_NAV_KEY
import com.homelab.appointmentadmin.data.USER_NAV_KEY
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

        observeStoredState()
    }

    fun saveCustomer() {
        if (isValid()) {
            viewModel.createUser()
        }
    }

    fun closeFragment() {
        findNavController().navigateUp()
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

    private fun observeStoredState() {
        viewModel.userStored.observe(viewLifecycleOwner) { stored ->
            val text: String
            val color: Int

            if (stored) {
                text = getString(R.string.user_creation_success)
                color = Color.parseColor(getString(R.color.teal_200))
            } else {
                text = getString(R.string.user_creation_err)
                color = Color.parseColor(getString(R.color.email_red))
            }

            Snackbar.make(binding.saveInfoBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(color)
                .setAction("Ok") {
                    informForNewUser()
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (stored) closeFragment()
                    }
                })
                .show()
        }
    }

    private fun informForNewUser() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            NEW_USER_NAV_KEY,
            viewModel.getUser()
        )
    }

}