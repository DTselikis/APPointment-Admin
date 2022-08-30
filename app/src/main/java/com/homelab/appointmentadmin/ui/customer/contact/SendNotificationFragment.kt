package com.homelab.appointmentadmin.ui.customer.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.homelab.appointmentadmin.R
import com.homelab.appointmentadmin.databinding.FragmentSendNotificationBinding
import kotlinx.coroutines.flow.collectLatest

class SendNotificationFragment : BottomSheetDialogFragment() {

    enum class NotificationItem(val code: Int) {
        CANCELLATION(0),
        CUSTOM(1)
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
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        id: Long
                    ) {
                        customNotificationGroup.visibility = View.VISIBLE
                        customNotificationTitleText.setText(getString(R.string.appointment_cancellation_title))
                        customNotificationMessageText.setText(getString(R.string.appointment_cancellation_message))
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        customNotificationGroup.visibility = View.GONE
                        customNotificationTitleText.setText("")
                        customNotificationMessageText.setText("")
                    }
                }
            }
        }

        observeNotificationSent()
    }

    fun sendNotification() {
        viewModel.sendNotification(args.token)
    }

    private fun observeNotificationSent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.notificationSent.collectLatest { sent ->
                val text =
                    if (sent) getString(R.string.notification_sent) else getString(R.string.notification_not_sent)

                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}