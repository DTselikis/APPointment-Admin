package com.homelab.appointmentadmin.ui.customer.contact

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerContactBinding

class CustomerContactFragment : Fragment() {

    private lateinit var binding: FragmentCustomerContactBinding
    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_contact, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            user = sharedViewModel.user
            fragmentCustomerContact = this@CustomerContactFragment
        }
    }

    fun callCustomer() {
        val phone = Uri.parse("tel:${sharedViewModel.user.phone}")
        val phoneIntent = Intent(Intent.ACTION_DIAL, phone)
        phoneIntent.resolveActivity(requireContext().packageManager)?.let {
            try {
                startActivity(phoneIntent)
            } catch (e: ActivityNotFoundException) {
                showWarningDialog(getString(R.string.dial_app))
            }

        }
    }

    fun sendEmail() {
        val email = Uri.parse("mailto:${sharedViewModel.user.email}")
        val emailIntent = Intent(Intent.ACTION_SENDTO, email)
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.email_signature))
        emailIntent.resolveActivity(requireContext().packageManager)?.let {
            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                showWarningDialog("Email")
            }
        }
    }

    private fun showWarningDialog(appName: String) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.setMessage(getString(R.string.opening_app_problem, appName))
            ?.setPositiveButton(getString(R.string.dismiss_warning_dialog)) { dialog, id ->
                dialog.dismiss()
            }
            ?.create()?.show()
    }
}