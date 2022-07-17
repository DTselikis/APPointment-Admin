package com.homelab.appointmentadmin.ui.customer.edit

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerProfileEditBinding
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel

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
        observeSaveStatus()
    }

    fun saveChanges() {
        if (viewModel.isModified()) {
            viewModel.storeChangesToDB()
        }
    }

    private fun closeEditsFragment() {
        findNavController().navigateUp()
    }

    private fun observeForEdits() {
        viewModel.firstname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.lastname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.nickname.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.phone.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.email.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
        viewModel.fbName.observe(viewLifecycleOwner) { viewModel.acknowledgeModifications() }
    }

    private fun observeSaveStatus() {
        viewModel.changesSaved.observe(viewLifecycleOwner) { saved ->
            val text: String
            val color: Int

            if (saved) {
                text = getString(R.string.save_successful)
                color = Color.parseColor(getString(R.color.teal_200))
            } else {
                text = getString(R.string.save_failed)
                color = Color.parseColor(getString(R.color.email_red))
            }

            Snackbar.make(binding.saveEditsBtn, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setBackgroundTint(color)
                .setAction("Ok") { closeEditsFragment() }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        closeEditsFragment()
                    }
                })
        }
    }

}