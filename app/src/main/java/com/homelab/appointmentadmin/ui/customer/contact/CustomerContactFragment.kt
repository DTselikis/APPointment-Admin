package com.homelab.appointmentadmin.ui.customer.contact

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                        it,
                        null,
                        { copyTextToClipboard(it) }
                    ) { ContactProvider.callCustomer(requireContext(), it) }
                )
            }

            customer.fbName?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.fb_blue,
                        R.drawable.facebook_logo,
                        it,
                        null,
                        { copyTextToClipboard(it) }
                    ) { ContactProvider.chatOnFacebook(requireContext(), customer.fbProfileId!!) }
                )
            }

            customer.email?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.email_red,
                        R.drawable.ic_email_white_24,
                        it,
                        null,
                        { copyTextToClipboard(it) }
                    ) {
                        try {
                            ContactProvider.sendEmail(requireContext(), it)
                        } catch (e: ActivityNotFoundException) {
                            showWarningDialog("mail")
                        }
                    }
                )
            }

            customer.token?.let {
                contactProviders.add(
                    ContactProviderInfo(
                        R.color.notification_cyan,
                        R.drawable.ic_notifications_24,
                        getString(R.string.send_notification),
                        null,
                        { copyTextToClipboard(it) }
                    ) { navigateToSendNotification() }
                )
            }
        }

        return contactProviders.toList()
    }

    private fun copyTextToClipboard(text: String): Boolean {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("social info", text))

        Toast.makeText(
            requireContext(),
            getString(R.string.copied_to_clipboard),
            Toast.LENGTH_SHORT
        ).show()

        return true
    }

    fun navigateToSendNotification() {
        val action =
            CustomerProfileFragmentDirections.actionCustomerProfileFragmentToSendNotificationFragment(
                sharedViewModel.user.value!!.token!!,
                sharedViewModel.user.value!!.uid!!
            )
        findNavController().navigate(action)
    }

    private fun searchOnPlayStore(query: String) {
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://play.google.com/store/search?q=$query&c=apps")
        }

        startActivity(playStoreIntent)
    }

    private fun showWarningDialog(appType: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.missing_app))
                .setMessage(getString(R.string.install_missing_app))
                .setPositiveButton(getString(R.string.dialog_yes_btn)) { _, _ ->
                    searchOnPlayStore(appType)
                }
                .setNegativeButton(getString(R.string.dialog_no_btn)) { _, _ -> }
                .create()
                .show()
        }
    }
}