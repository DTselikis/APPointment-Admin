package com.homelab.appointmentadmin.ui.customer.contact

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentCustomerContactBinding
import com.homelab.appointmentadmin.model.ContactProviderInfo
import com.homelab.appointmentadmin.ui.customer.CustomerProfileFragmentDirections
import com.homelab.appointmentadmin.ui.customer.CustomerProfileSharedViewModel
import com.homelab.appointmentadmin.utils.ContactProvider

class CustomerContactFragment : Fragment() {
    private val sharedViewModel: CustomerProfileSharedViewModel by activityViewModels()

    private lateinit var binding: FragmentCustomerContactBinding

    private lateinit var contactProvidersAdapter: ContactProvidersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_contact, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactProvidersAdapter = ContactProvidersAdapter()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            customerContactRv.adapter = contactProvidersAdapter
        }

        contactProvidersAdapter.submitList(constructContactProviders())
    }

    private fun constructContactProviders(): List<ContactProviderInfo> {
        val contactProviders = mutableListOf<ContactProviderInfo>()
        sharedViewModel.user.value?.let { customer ->
            customer.phone?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.phone_green,
                        R.drawable.ic_phone_24,
                        it
                    ) {
                        ContactProvider.callCustomer(requireContext(), it)
                    }
                )
            }

            customer.fbName?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.fb_blue,
                        R.drawable.facebook_logo,
                        it
                    ) {
                        try {
                            ContactProvider.openFacebookChat(requireContext(), customer.fbProfileId!!)
                        } catch (e: ActivityNotFoundException) {
                            showWarningDialog("Facebook")
                        }
                    }
                )
            }

            customer.email?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.email_red,
                        R.drawable.ic_email_white_24,
                        it
                    ) {
                        try {
                            ContactProvider.sendEmail(requireContext(), it)
                        } catch (e: ActivityNotFoundException) {
                            showWarningDialog("email")
                        }
                    }
                )
            }

            customer.token?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.notification_cyan,
                        R.drawable.ic_notifications_24,
                        getString(R.string.send_notification)
                    ) {
                        navigateToSendNotification()
                    }
                )
            }
        }

        return contactProviders.toList()
    }

//    fun callCustomer() {
//        val phone = Uri.parse("tel:${sharedViewModel.user.value!!.phone}")
//        val phoneIntent = Intent(Intent.ACTION_DIAL, phone)
//        phoneIntent.resolveActivity(requireContext().packageManager)?.let {
//            try {
//                startActivity(phoneIntent)
//            } catch (e: ActivityNotFoundException) {
//                showWarningDialog(getString(R.string.dial_app))
//            }
//
//        }
//    }
//
//    fun sendEmail() {
//        val email = Uri.parse("mailto:${sharedViewModel.user.value!!.email}")
//        val emailIntent = Intent(Intent.ACTION_SENDTO, email)
//            .putExtra(Intent.EXTRA_TEXT, getString(R.string.email_signature))
//        emailIntent.resolveActivity(requireContext().packageManager)?.let {
//            try {
//                startActivity(emailIntent)
//            } catch (e: ActivityNotFoundException) {
//                showWarningDialog("Email")
//            }
//        }
//    }

    fun navigateToSendNotification() {
        val action =
            CustomerProfileFragmentDirections.actionCustomerProfileFragmentToSendNotificationFragment(
                sharedViewModel.user.value!!.token!!,
                sharedViewModel.user.value!!.uid!!
            )
        findNavController().navigate(action)
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