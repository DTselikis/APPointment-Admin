package com.homelab.appointmentadmin.ui.customer.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentSendNotificationBinding
import kotlinx.coroutines.flow.collectLatest

class SendNotificationFragment : BottomSheetDialogFragment() {

    enum class NotificationType(val code: Int) {
        CANCELLATION(0),
        CUSTOM(1)
    }

    enum class NotificationStatus(val code: Int) {
        SENT(0),
        READ(1)
    }

    private val args: SendNotificationFragmentArgs by navArgs<SendNotificationFragmentArgs>()

    private val viewModel: SendNotificationViewModel by viewModels()

    private lateinit var binding: FragmentSendNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_send_notification, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            sendNotificationFragment = this@SendNotificationFragment
            viewModel = this@SendNotificationFragment.viewModel
            preMadeNotificationsMenu.apply {
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        resources.getStringArray(R.array.pre_made_notifications)
                    )
                )

                setOnItemClickListener { _, _, position, _ ->
                    val (title, message, type) = when (position) {
                        NotificationType.CANCELLATION.code -> {
                            customNotificationGroup.visibility = View.GONE
                            Triple(
                                getString(R.string.appointment_cancellation_title),
                                getString(R.string.appointment_cancellation_message),
                                NotificationType.CANCELLATION.code
                            )
                        }
                        NotificationType.CUSTOM.code -> {
                            customNotificationGroup.visibility = View.VISIBLE
                            Triple("", "", NotificationType.CUSTOM.code)
                        }
                        else -> Triple("", "", -1)
                    }

                    customNotificationTitleText.setText(title)
                    customNotificationTitle.tag = type
                    customNotificationMessageText.setText(message)
                }
            }
        }

        observeNotificationSent()
    }

    private fun closeDialog() {
        findNavController().navigateUp()
    }

    fun sendNotification() {
        binding.sendNotificationBtn.isEnabled = false
        viewModel.sendNotification(args.token, args.uid, binding.customNotificationTitle.tag as Int)
    }

    private fun observeNotificationSent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.notificationSent.collectLatest { sent ->
                if (sent) {
                    val text = getString(R.string.notification_sent)
                    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                    closeDialog()
                } else {
                    binding.sendNotificationBtn.isEnabled = true
                    val text = getString(R.string.notification_not_sent)
                    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}